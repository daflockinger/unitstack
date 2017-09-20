package com.flockinger.unitstack.sqs;

import java.util.HashMap;
import java.util.Map;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.response.ResponderFactory;
import com.flockinger.unitstack.utils.MessageUtils;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class SqsRequestTransformer extends ResponseDefinitionTransformer {

  public final static String SQS_REQUEST_TRANSFORMER="SQS_REQUEST_TRANSFORMER";
  public final static String PARAMETER_URL_NAME = "__url";
  
  private Responder sqsResponder;
  private MessageUtils utils;
  private Map<String, AwsQueue> queues;
  
  public SqsRequestTransformer(Map<String, AwsQueue> queues) {
    sqsResponder = ResponderFactory.sqsResponder();
    utils = new MessageUtils();
    this.queues = queues;
  }
  
  @Override
  public String getName() {
    return SQS_REQUEST_TRANSFORMER;
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
      FileSource files, Parameters parameters) {
    MockParameters params = (MockParameters) parameters.get(UnitStackTest.MOCK_PARAMS);
    Map<String, String> body = new HashMap<>(utils.queryStringToMap(request.getBodyAsString()));
    body.put(PARAMETER_URL_NAME, request.getUrl());
    
    MockResponse response = sqsResponder.createResponse(new MockRequest(body, params, utils).withQueues(queues));
    
    return new ResponseDefinitionBuilder()
        .withBody(response.getBody())
        .withStatus(response.getStatus())
        .build();
  }
}
