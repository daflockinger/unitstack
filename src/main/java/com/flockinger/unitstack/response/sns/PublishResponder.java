package com.flockinger.unitstack.response.sns;

import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.SnsMessage;
import com.flockinger.unitstack.model.sns.Topic;

public class PublishResponder extends SnsResponder {

  private final static String PUBLISH_ACTION = "Publish";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(PUBLISH_ACTION,request);
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
    return new MockResponse(request.utils().successBody(PUBLISH_ACTION, "<MessageId>" + messageId + "</MessageId>"));
  }

}
