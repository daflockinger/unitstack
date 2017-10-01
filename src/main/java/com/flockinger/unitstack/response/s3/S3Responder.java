package com.flockinger.unitstack.response.s3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

abstract class S3Responder implements Responder {
  public abstract boolean isSameAction(MockRequest request);

  public abstract MockResponse createResponse(MockRequest request);

  private final static Pattern bucketFromUrlPattern =
      Pattern.compile("^(?:http|https)://([^\\.[^l]]*)."
          + StringUtils.substringAfterLast(UnitStackTest.UNIT_STACK_URL, "/") + ":"
          + UnitStackTest.S3_PORT + "(?:.*)$");


  protected String getBucketFromUrl(MockRequest request) {
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);
    String bucketName = null;
    Matcher matcher = bucketFromUrlPattern.matcher(url);

    if (matcher.find()) {
      bucketName = matcher.group(1);
    }
    return bucketName;
  }

  protected String successBody(String action, String content) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<" + action
        + "Result xmlns=\"http://s3.amazonaws.com/doc/2006-03-01\">\n" + content + "</" + action
        + "Result>";
  }
}
