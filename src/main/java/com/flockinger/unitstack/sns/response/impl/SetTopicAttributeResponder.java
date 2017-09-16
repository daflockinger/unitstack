package com.flockinger.unitstack.sns.response.impl;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class SetTopicAttributeResponder extends SnsResponder {

  private final static String SET_TOPIC_ATTR_ACTION = "SetTopicAttributes";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return SET_TOPIC_ATTR_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String attributeName = request.getBodyParameters().get("AttributeName");
    String attributeValue = request.getBodyParameters().get("AttributeValue");
    
    if(request.getTopics().containsKey(topicArn)) {
      Topic topic = request.getTopics().get(topicArn);
      topic.getAttributes().put(attributeName, attributeValue);
    }
    return new MockResponse(successBody(SET_TOPIC_ATTR_ACTION, null));
  }
}
