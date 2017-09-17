package com.flockinger.unitstack.response.sqs;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.model.sqs.SqsMessage;

import wiremock.org.apache.commons.lang3.StringUtils;

public class SendMessageResponder extends SqsResponder {

  private final static String SEND_MESSAGE_ACTION = "SendMessage";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(SEND_MESSAGE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueUrl = request.getBodyParameters().get("QueueUrl");
    String messageBody = request.utils().decodeValue(request.getBodyParameters().get("MessageBody"));
    Optional<AwsQueue> maybeQueue = findByUrl(queueUrl, request.getQueues().values());
    
    if(maybeQueue.isPresent()) {
      SqsMessage message = new SqsMessage();
      message.setBody(messageBody);
      message.setMd5(request.utils().getMD5(messageBody));
      message.setReceiptHandle(UUID.randomUUID().toString());
      message.setId(UUID.randomUUID().toString());
      maybeQueue.get().getMessageQueue().add(message);
    }
    
    //TODO continue there!!
    return null;
  }
  
  private Optional<AwsQueue> findByUrl(String queueUrl, Collection<AwsQueue> queues) {
    return queues.stream().filter(queue -> StringUtils.equals(queueUrl, queue.getUrl())).findFirst();
  }

}
