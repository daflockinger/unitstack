package com.flockinger.unitstack.sns.response.impl;

import java.util.Map;
import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class CreateTopicResponder extends SnsResponder {

  private final static String CREATE_TOPIC_ACTION = "CreateTopic";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return CREATE_TOPIC_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Topic topic = new Topic();
    Map<String,String> body = request.getBodyParameters();
    topic.setName(body.get("Name"));
    topic.setTopicArn("arn:aws:sns:us-east-1:"+topic.getName()+":" + UUID.randomUUID().toString());
    request.getTopics().put(topic.getTopicArn(), topic);
    
    return new MockResponse(successBody(CREATE_TOPIC_ACTION, "<TopicArn>"+topic.getTopicArn()+"</TopicArn>"));
  }

}
