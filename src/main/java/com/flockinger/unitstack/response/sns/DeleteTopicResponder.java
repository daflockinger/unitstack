package com.flockinger.unitstack.response.sns;

import java.util.Map;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class DeleteTopicResponder extends SnsResponder {

  private final static String DELETE_TOPIC_ACTION = "DeleteTopic";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(DELETE_TOPIC_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Map<String,String> body = request.getBodyParameters();
    String topicArnToDelete =request.utils().decodeValue(body.get("TopicArn"));
   
    if(request.getTopics().containsKey(topicArnToDelete)) {
      request.getTopics().remove(topicArnToDelete);
    }
    return new MockResponse(request.utils().successBody(DELETE_TOPIC_ACTION, null));
  }

}
