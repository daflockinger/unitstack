package com.flockinger.unitstack.transformer;


import java.util.Map;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.response.ResponderFactory;
import com.flockinger.unitstack.utils.MessageUtils;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;

public class SnsRequestTransformer extends ResponseDefinitionTransformer {

  public final static String SNS_REQUEST_TRANSFORMER="SNS_REQUEST_TRANSFORMER";
  
  private Map<String, Topic> topics;
  private Responder snsResponder;
  private MessageUtils utils;
    
  public SnsRequestTransformer(Map<String, Topic> topics) {
    this.topics = topics;
    snsResponder = ResponderFactory.snsResponder();
    utils = new MessageUtils();
  }
  
  public String getName() {
    return SNS_REQUEST_TRANSFORMER;
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
      FileSource files, Parameters parameters) {
    MockParameters params = (MockParameters) parameters.get(UnitStackTest.MOCK_PARAMS);
    Map<String, String> body = utils.queryStringToMap(request.getBodyAsString());
    MockResponse response = snsResponder.createResponse(new MockRequest(body, params,utils).withTopics(topics));
    
    return new ResponseDefinitionBuilder()
        .withBody(response.getBody())
        .withStatus(response.getStatus())
        .build();
  }
}
