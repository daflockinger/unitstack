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
package com.flockinger.unitstack.transformer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.response.ResponderFactory;
import com.flockinger.unitstack.utils.MessageUtils;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.HttpHeader;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import wiremock.org.apache.commons.lang3.StringUtils;

public class S3RequestTransformer extends ResponseDefinitionTransformer {

  public final static String S3_REQUEST_TRANSFORMER = "S3_REQUEST_TRANSFORMER";
  public final static String PARAMETER_URL_NAME = "__url";
  public final static String PARAMETER_METHOD = "__method";
  public final static String PARAMETER_RESPONSE_XML = "__xml";
  public final static String ACTION = "__action";

  private Responder s3Responder;
  private Map<String, Bucket> buckets;
  private MessageUtils utils;

  public S3RequestTransformer(Map<String, Bucket> buckets) {
    super();
    s3Responder = ResponderFactory.s3Responder();
    this.buckets = buckets;
    utils = new MessageUtils();
  }

  @Override
  public String getName() {
    return S3_REQUEST_TRANSFORMER;
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
      FileSource files, Parameters parameters) {
    MockParameters params = (MockParameters) parameters.get(UnitStackTest.MOCK_PARAMS);
    Map<String, String> body = new HashMap<>();
    String method = request.getMethod().getName();
    byte[] fileContent = null;
    body.put(PARAMETER_URL_NAME, request.getAbsoluteUrl());
    body.put(PARAMETER_METHOD, method);
    if (method.equals("GET")) {
      body.put(ACTION, StringUtils.substringAfter(request.getAbsoluteUrl(), "?"));
    } else {
      body.put(PARAMETER_RESPONSE_XML, request.getBodyAsString());
      fileContent = request.getBody();
    }
    body.putAll(getHeaders(request));

    MockResponse response = s3Responder.createResponse(
        new MockRequest(body, params, utils).withBuckets(buckets).withFileContent(fileContent));

    return createResponse(response);
  }

  private ResponseDefinition createResponse(MockResponse response) {
    if (response.getBinaryBody() != null) {
      return new ResponseDefinitionBuilder().withBody(response.getBinaryBody())
          .withStatus(response.getStatus())
          .withHeader("Content-Length", Integer.toString(response.getBinaryBody().length)).build();
    }
    return new ResponseDefinitionBuilder().withBody(response.getBody())
        .withStatus(response.getStatus()).build();
  }

  private Map<String, String> getHeaders(Request request) {
    return request.getHeaders().all().stream()
        .collect(Collectors.toMap(HttpHeader::key, HttpHeader::firstValue));
  }

}
