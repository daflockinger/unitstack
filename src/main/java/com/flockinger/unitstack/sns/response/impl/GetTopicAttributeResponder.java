package com.flockinger.unitstack.sns.response.impl;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class GetTopicAttributeResponder extends SnsResponder {

  private final static String GET_TOPIC_ATTR_ACTION = "GetTopicAttributes";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return GET_TOPIC_ATTR_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String responseContent = null;
    
    if(request.getTopics().containsKey(topicArn)) {
      Topic topic = request.getTopics().get(topicArn);
      responseContent = getAttributeResponseXml(topic.getAttributes());
    }
    return new MockResponse(successBody(GET_TOPIC_ATTR_ACTION, responseContent));
  }
}
