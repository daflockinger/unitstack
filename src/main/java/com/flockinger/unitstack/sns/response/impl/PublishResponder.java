package com.flockinger.unitstack.sns.response.impl;

import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.SnsMessage;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class PublishResponder extends SnsResponder {

  private final static String PUBLISH_ACTION = "Publish";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return PUBLISH_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String messageId = UUID.randomUUID().toString();
    String topicArn = request.getBodyParameters().get("TopicArn");
    
    if(request.getTopics().containsKey(topicArn)) {
      Topic topic = request.getTopics().get(topicArn);
      SnsMessage message = new SnsMessage(); 
      message.setBody(request.getBodyParameters().get("Message"));
      message.setId(messageId);
      message.setStructure(request.getBodyParameters().get("MessageStructure"));
      message.setSubject(request.getBodyParameters().get("Subject"));
      topic.getMessages().add(message);
    }
    return new MockResponse(successBody(PUBLISH_ACTION, "<MessageId>" + messageId + "</MessageId>"));
  }

}
