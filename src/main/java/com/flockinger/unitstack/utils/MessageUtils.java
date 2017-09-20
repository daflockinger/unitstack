package com.flockinger.unitstack.utils;

import static com.amazonaws.util.StringUtils.UTF8;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.Md5Utils;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.google.common.base.Splitter;

public class MessageUtils {

  private final static String ACTION_NAME = "Action";

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
}
