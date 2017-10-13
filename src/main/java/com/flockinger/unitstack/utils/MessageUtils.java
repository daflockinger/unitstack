/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.utils;

import static com.amazonaws.util.StringUtils.UTF8;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
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

import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.Md5Utils;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.ISO8601DateFormat;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationModule;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;


/**
 * Internal utility class for common data handling
 * of received data by the stubs.
 */
@SuppressWarnings("deprecation")
public class MessageUtils {

  private final static String ACTION_NAME = "Action";
  private final ExtendedXmlMapper xmlMapper;
  private final Map<String,String> XML_NAMESPACE_PREFIXES = ImmutableMap.of("xsi","http://www.w3.org/2001/XMLSchema-instance");
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
   * @param action
   * @param content
   * @return
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
   * @param params
   * @return
   */
  public String errorBody(MockParameters params) {
    return errorBody(params.getErrorMessage(), params.getSnsException());
  }

  /**
   * Returns common error response XML with custom message and code by exception.
   * 
   * @param errorMessage
   * @param exception
   * @return
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
   * @param action
   * @param request
   * @return
   */
  public boolean hasRequestAction(final String action, MockRequest request) {
    return StringUtils.equals(action, getAction(request.getBodyParameters()));
  }

  /**
   * Returns action value from request Map.
   * 
   * @param requestBody
   * @return
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
   * @return
   */
  private String shortUid() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  /**
   * Returns URL decoded value, fail save.
   * 
   * @param value
   * @return
   */
  public String decodeValue(String value) {
    try {
      if (value != null) {
        value = URLDecoder.decode(value, "utf-8");
      }
    } catch (UnsupportedEncodingException e) {
      LOG.error("System doesn't support UTF-8, should never happen!",e);
    }
    return value;
  }

  
  /**
   * Returns a <code>Map&lt;String,String&gt;</code> from a (encoded) URL query string.
   * 
   * @param query
   *           URL query
   * @return
   */
  public Map<String, String> queryStringToMap(String query) {
    if(StringUtils.isEmpty(query)) {
      return new HashMap<>();
    }
    assertTrue("Not a valid URL query string!", query.contains("="));
    try {
      query = URLDecoder.decode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      LOG.error("System doesn't support UTF-8, should never happen!",e);
    }
    return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
  }

  /**
   * Calculates the MD5 hex code of a string (using the util class<br>
   * that the AWS SDK uses for MD5 calculation).
   * 
   * @param message
   * @return
   */
  public String getMD5(String message) {
    byte[] expectedMd5 = Md5Utils.computeMD5Hash(message.getBytes(UTF8));
    return BinaryUtils.toHex(expectedMd5);
  }
  
  /**
   * Converts an object fail-save to an XML string.<br>
   * It returns an empty string, if the conversion should fail.
   * 
   * @param object
   * @return
   */
  public <T> String toXmlString(T object) {
    String xml = "";
    
    try {
      xml = xmlMapper.writeValueAsString(object,XML_NAMESPACE_PREFIXES);
    } catch (Exception e) {
      LOG.error("Cannot serialize object to XML!",e);
    }
    return xml;
  }
  
  /**
   * Converts a XML string fail-save to an object.<br>
   * It returns an empty optional, if the conversion should fail.
   * 
   * @param xml
   * @param type
   * @return
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
   * @param values
   * @return
   */
  public boolean areAllPresent(Optional<?>... values) {
    return Arrays.asList(values).stream().allMatch(Optional::isPresent);
  }
  
  /**
   * Converts chunked response data (returned by in the request body) <br>
   * into a single piece, regular byte array.
   * 
   * @param content
   *           Chunked data
   * @return
   */
  public byte[] unchunkResponse(byte[] content) {
    SessionInputBufferImpl buffer =
        new SessionInputBufferImpl(new HttpTransportMetricsImpl(), 1024);
    buffer.bind(new ByteArrayInputStream(content));

    try {
      if(content != null && content.length > 0) {
        return IOUtils.toByteArray(new ChunkedInputStream(buffer));
      }
    } catch (IOException e) {
      LOG.error("Cannot write chunked input stream to byte array!",e);
    }
    return content;
  }
}
