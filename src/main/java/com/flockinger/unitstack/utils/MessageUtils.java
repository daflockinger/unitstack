/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.utils;

import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.io.ChunkedInputStream;
import org.apache.http.impl.io.HttpTransportMetricsImpl;
import org.apache.http.impl.io.SessionInputBufferImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.BaseEncoding;


/**
 * Internal utility class for common data handling of received data by the stubs.
 */
@SuppressWarnings("deprecation")
public class MessageUtils {

  private final static String ACTION_NAME = "Action";
  private final ExtendedXmlMapper xmlMapper;
  private final Map<String, String> XML_NAMESPACE_PREFIXES =
      ImmutableMap.of("xsi", "http://www.w3.org/2001/XMLSchema-instance");
  private final static Logger LOG = LoggerFactory.getLogger(MessageUtils.class);


  public MessageUtils() {
    xmlMapper = new ExtendedXmlMapper();
    xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    xmlMapper.setDateFormat(new ISO8601DateFormat());
    xmlMapper.registerModule(new JaxbAnnotationModule());
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
  }

  /**
   * Returns a successfull XML response defined by the <code>action</code>, <br>
   * containing optional XML <code>content</code>.
   * 
   * @param action Request action name
   * @param content Response XML content message
   * @return Full response XML for action
   */
  public String successBody(String action, String content) {
    if (content == null) {
      content = "<MessageId>" + shortUid() + "</MessageId>";
    }

    String response =
        "<" + action + "Response xmlns=\"http://sns.amazonaws.com/doc/2010-03-31/\">" + "<" + action
            + "Result> " + content + "</" + action + "Result>" + "<ResponseMetadata><RequestId>"
            + shortUid() + "</RequestId></ResponseMetadata>" + "</" + action + "Response>";

    return response;
  }

  /**
   * Returns error XML out of {@link MockParameters}
   * 
   * @param params Predefined MockParameters
   * @return Error response XML
   */
  public String errorBody(MockParameters params) {
    return errorBody(params.getErrorMessage(), params.getSnsException());
  }

  /**
   * Returns common error response XML with custom message and code by exception.
   * 
   * @param errorMessage Error response message
   * @param exception Mock response exception
   * @return Default error body XML message
   */
  public String errorBody(String errorMessage, Class<?> exception) {
    return "<ErrorResponse xmlns=\"http://sns.amazonaws.com/doc/2010-03-31/\"><Error>\n"
        + "        <Type>Sender</Type>\n" + "        <Code>" + exceptionToCodeName(exception)
        + "</Code>\n" + "        <Message>" + errorMessage + "</Message>\n"
        + "        </Error><RequestId>" + shortUid() + "</RequestId>\n"
        + "        </ErrorResponse>";
  }

  /**
   * Returns true if the request has an action.
   * 
   * @param action The action parameter to look for
   * @param request Mock request data
   * @return True if request contains given action
   */
  public boolean hasRequestAction(final String action, MockRequest request) {
    return StringUtils.equals(action, getAction(request.getBodyParameters()));
  }

  /**
   * Returns action value from request Map.
   * 
   * @param requestBody Request map
   * @return Action from request key/value map
   */
  public String getAction(Map<String, String> requestBody) {
    return requestBody.get(ACTION_NAME);
  }

  private String exceptionToCodeName(Class<?> exception) {
    return exception.getSimpleName().replaceAll("Exception", "");
  }

  /**
   * Generates a random 8-digit UUID code.
   * 
   * @return Random 8 digit UUID
   */
  private String shortUid() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Returns URL decoded value, fail save.
   * 
   * @param value Encoded message
   * @return Decoded message
   */
  public String decodeValue(String value) {
    try {
      if (value != null) {
        value = URLDecoder.decode(value, "utf-8");
      }
    } catch (UnsupportedEncodingException e) {
      LOG.error("System doesn't support UTF-8, should never happen!", e);
    }
    return value;
  }


  /**
   * Returns a <code>Map&lt;String,String&gt;</code> from a (encoded) URL query string.
   * 
   * @param query URL query
   * @return Map of query name/value pairs
   */
  public Map<String, String> queryStringToMap(String query) {
    if (StringUtils.isEmpty(query)) {
      return new HashMap<>();
    }
    assertTrue("Not a valid URL query string!", query.contains("="));
    try {
      query = URLDecoder.decode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error("System doesn't support UTF-8, should never happen!", e);
    }
    return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
  }

  /**
   * Calculates the MD5 hex code of a string (using the util class<br>
   * that the AWS SDK uses for MD5 calculation).
   * 
   * @param message Any text
   * @return Upper-case MD5 checksum in HEX string format
   */
  public String getMD5(String message) {
    String md5Hash = "";
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("MD5");
      byte[] hashBytes = digest.digest(message.getBytes(StandardCharsets.UTF_8.name()));
      md5Hash = BaseEncoding.base16().encode(hashBytes).toLowerCase();
    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
      LOG.error("MD5 Algorithm not supported by your OS, should never happen!");
    }
    return md5Hash;
  }

  /**
   * Converts an object fail-save to an XML string.<br>
   * It returns an empty string, if the conversion should fail.
   * 
   * @param object Serializable Object
   * @param <T> Type to convert
   * @return XML string
   */
  public <T> String toXmlString(T object) {
    String xml = "";

    try {
      xml = xmlMapper.writeValueAsString(object, XML_NAMESPACE_PREFIXES);
    } catch (Exception e) {
      LOG.error("Cannot serialize object to XML!", e);
    }
    return xml;
  }

  /**
   * Converts a XML string fail-save to an object.<br>
   * It returns an empty optional, if the conversion should fail.
   * 
   * @param xml XML message
   * @param type Object type
   * @param <T> Type to be converted to
   * @return Optional deserialized, typed object
   */
  public <T> Optional<T> fromXmlTo(String xml, Class<T> type) {
    T object = null;

    try {
      object = xmlMapper.readValue(xml, type);
    } catch (IOException e) {
      LOG.error("Cannot parse XML String to " + type, e);
    }
    return Optional.ofNullable(object);
  }

  /**
   * Returns true if all optional values are present (not null/empty).<br>
   * If at least one is not present, it returns false.
   * 
   * @param values Many optionals
   * @return True if all are present
   */
  public boolean areAllPresent(Optional<?>... values) {
    return Arrays.asList(values).stream().allMatch(Optional::isPresent);
  }

  /**
   * Converts chunked response data (returned by in the request body) <br>
   * into a single piece, regular byte array.
   * 
   * @param content Chunked data byte array
   * @return Unchunked (regular) byte array
   */
  public byte[] unchunkResponse(byte[] content) {
    SessionInputBufferImpl buffer =
        new SessionInputBufferImpl(new HttpTransportMetricsImpl(), 1024);
    buffer.bind(new ByteArrayInputStream(content));

    try {
      if (content != null && content.length > 0) {
        return IOUtils.toByteArray(new ChunkedInputStream(buffer));
      }
    } catch (IOException e) {
      LOG.error("Cannot write chunked input stream to byte array!", e);
    }
    return content;
  }
}
