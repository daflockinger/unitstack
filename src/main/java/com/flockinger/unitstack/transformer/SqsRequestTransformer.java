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

  public final static String SQS_REQUEST_TRANSFORMER = "SQS_REQUEST_TRANSFORMER";
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

    MockResponse response =
        sqsResponder.createResponse(new MockRequest(body, params, utils).withQueues(queues));

    return new ResponseDefinitionBuilder().withBody(response.getBody())
        .withStatus(response.getStatus()).build();
  }
}
