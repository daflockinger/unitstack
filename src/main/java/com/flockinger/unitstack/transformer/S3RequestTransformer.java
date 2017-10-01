package com.flockinger.unitstack.transformer;

import java.util.HashMap;
import java.util.Map;

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
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

import wiremock.org.apache.commons.lang3.StringUtils;

public class S3RequestTransformer extends ResponseDefinitionTransformer {

  public final static String S3_REQUEST_TRANSFORMER="S3_REQUEST_TRANSFORMER";
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
    body.put(PARAMETER_URL_NAME, request.getAbsoluteUrl());
    body.put(PARAMETER_METHOD,method);
    if(method.equals("GET")) {
      body.put(ACTION, StringUtils.substringAfter(request.getAbsoluteUrl(), "?"));
    } else {
      body.put(PARAMETER_RESPONSE_XML, request.getBodyAsString());
    }
    
    MockResponse response = s3Responder.createResponse(new MockRequest(body, params, utils)
        .withBuckets(buckets).withFiles(files));
    
    return new ResponseDefinitionBuilder()
        .withBody(response.getBody())
        .withStatus(response.getStatus())
        .build();
  }

}
