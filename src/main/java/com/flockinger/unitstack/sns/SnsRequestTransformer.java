package com.flockinger.unitstack.sns;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.SnsMockParameters;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponderFactory;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.ResponseDefinition;
import com.google.common.base.Splitter;

public class SnsRequestTransformer extends ResponseDefinitionTransformer {

  public final static String SNS_REQUEST_TRANSFORMER="SNS_REQUEST_TRANSFORMER";
  
  private Map<String, Topic> topics;
  private SnsResponderFactory responderFactory;
    
  public SnsRequestTransformer(Map<String, Topic> topics) {
    this.topics = topics;
    responderFactory = new SnsResponderFactory();
  }
  
  public String getName() {
    return SNS_REQUEST_TRANSFORMER;
  }

  @Override
  public ResponseDefinition transform(Request request, ResponseDefinition responseDefinition,
      FileSource files, Parameters parameters) {
    SnsMockParameters params = (SnsMockParameters) parameters.get(UnitStackTest.MOCK_PARAMS);
    Map<String, String> body = queryStringToMap(request.getBodyAsString());
    MockResponse response = responderFactory.createResponse(new MockRequest(body, params, topics));
    
    return new ResponseDefinitionBuilder()
        .withBody(response.getBody())
        .withStatus(response.getStatus())
        .build();
  }
  
  private Map<String,String> queryStringToMap(String query){
    try {
      query = URLDecoder.decode(query, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    }
    return Splitter.on('&').trimResults().withKeyValueSeparator("=").split(query);
  }
}
