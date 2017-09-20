package com.flockinger.unitstack.response.sqs;

import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.model.sqs.SqsMessage;

public class SendMessageResponder extends SqsResponder {

  private final static String SEND_MESSAGE_ACTION = "SendMessage";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(SEND_MESSAGE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    String messageBody = request.utils().decodeValue(request.getBodyParameters().get("MessageBody"));
    SqsMessage message = new SqsMessage();
    message.setBody(messageBody);
    message.setMd5(request.utils().getMD5(messageBody));
    message.setId(UUID.randomUUID().toString());
    
    if(request.getQueues().containsKey(queueName)) {
      AwsQueue queue = request.getQueues().get(queueName);
      queue.getMessageQueue().add(message);
    }
    return new MockResponse(request.utils().successBody(SEND_MESSAGE_ACTION, getSendResponseXml(message)));
  }

  private String getSendResponseXml(SqsMessage message) {
    return "<MD5OfMessageBody>" + message.getMd5() + "</MD5OfMessageBody>" + "<MessageId>"
        + message.getId() + "</MessageId>";
  }
}
