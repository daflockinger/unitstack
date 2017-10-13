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


public class MessageUtils {

  private final static String ACTION_NAME = "Action";
  private final ExtendedXmlMapper xmlMapper;
  private final Map<String,String> XML_NAMESPACE_PREFIXES = ImmutableMap.of("xsi","http://www.w3.org/2001/XMLSchema-instance");
  
  public MessageUtils() {
    xmlMapper = new ExtendedXmlMapper();
    xmlMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    xmlMapper.setDateFormat(new ISO8601DateFormat());
    xmlMapper.registerModule(new JaxbAnnotationModule());
    xmlMapper.setSerializationInclusion(Include.NON_NULL);
  }

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

  public String errorBody(MockParameters params) {
    return errorBody(params.getErrorMessage(), params.getSnsException());
  }

  public String errorBody(String errorMessage, Class<?> exception) {
    return "<ErrorResponse xmlns=\"http://sns.amazonaws.com/doc/2010-03-31/\"><Error>\n"
        + "        <Type>Sender</Type>\n" + "        <Code>" + exceptionToCodeName(exception)
        + "</Code>\n" + "        <Message>" + errorMessage + "</Message>\n"
        + "        </Error><RequestId>" + shortUid() + "</RequestId>\n"
        + "        </ErrorResponse>";
  }

  public boolean hasRequestAction(final String action, MockRequest request) {
    return StringUtils.equals(action, getAction(request.getBodyParameters()));
  }

  public String getAction(Map<String, String> requestBody) {
    return requestBody.get(ACTION_NAME);
  }

  private String exceptionToCodeName(Class<?> exception) {
    return exception.getSimpleName().replaceAll("Exception", "");
  }

  private String shortUid() {
    return UUID.randomUUID().toString().substring(0, 8);
  }

  public String decodeValue(String value) {
    try {
      if (value != null) {
        value = URLDecoder.decode(value, "utf-8");
      }
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    } ;
    return value;
  }

  public String getAttributeResponseXml(Map<String, String> attributes) {
    String attributesXml = "";

    for (String attributeName : attributes.keySet()) {
      attributesXml += "<entry>" + "<key>" + attributeName + "</key>" + "<value>"
          + attributes.get(attributeName) + "</value>" + "</entry>";
    }
    return "<Attributes>" + attributesXml + "</Attributes>";
  }
  
  public Map<String, String> queryStringToMap(String query) {
    if(StringUtils.isEmpty(query)) {
      return new HashMap<>();
    }
    try {
      query = URLDecoder.decode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
  }

  public String getMD5(String message) {
    byte[] expectedMd5 = Md5Utils.computeMD5Hash(message.getBytes(UTF8));
    return BinaryUtils.toHex(expectedMd5);
  }
  
  public <T> String toXmlString(T object) {
    String xml = "";
    
    try {
      xml = xmlMapper.writeValueAsString(object,XML_NAMESPACE_PREFIXES);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return xml;
  }
  
  public <T> Optional<T> fromXmlTo(String xml, Class<T> type) {
    T object = null;
    
    try {
      object = xmlMapper.readValue(xml, type);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Optional.ofNullable(object);
  }
  
  public boolean areAllPresent(Optional<?>... values) {
    return Arrays.asList(values).stream().allMatch(Optional::isPresent);
  }
  
  public byte[] unchunkResponse(byte[] content) {
    SessionInputBufferImpl buffer =
        new SessionInputBufferImpl(new HttpTransportMetricsImpl(), 1024);
    buffer.bind(new ByteArrayInputStream(content));

    try {
      if(content != null && content.length > 0) {
        return IOUtils.toByteArray(new ChunkedInputStream(buffer));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return content;
  }
}
