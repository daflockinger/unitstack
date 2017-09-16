package com.flockinger.unitstack.sns.response.impl;

import java.util.Map;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class DeleteTopicResponder extends SnsResponder {

  private final static String DELETE_TOPIC_ACTION = "DeleteTopic";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return DELETE_TOPIC_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Map<String,String> body = request.getBodyParameters();
    String topicArnToDelete =decodeValue(body.get("TopicArn"));
   
    if(request.getTopics().containsKey(topicArnToDelete)) {
      request.getTopics().remove(topicArnToDelete);
    }
    return new MockResponse(successBody(DELETE_TOPIC_ACTION, null));
  }

}
