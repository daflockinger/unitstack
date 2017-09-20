package com.flockinger.unitstack.response.sqs;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.model.sqs.SqsMessage;

import wiremock.org.apache.commons.lang3.StringUtils;

public class DeleteMessageResponder extends SqsResponder {

  private final static String DELETE_MESSAGE_ACTION = "DeleteMessage";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(DELETE_MESSAGE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    String receiptHandle = request.getBodyParameters().get("ReceiptHandle");
    
    if(request.getQueues().containsKey(queueName)) {
      AwsQueue queue = request.getQueues().get(queueName);
      removeMessageFrom(queue.getMessageQueue(), receiptHandle);
      removeMessageFrom(queue.getInvisibilityQueueFor(receiptHandle), receiptHandle);
    }
    return new MockResponse(request.utils().successBody(DELETE_MESSAGE_ACTION, null));
  }

  private void removeMessageFrom(Queue<SqsMessage> messages, String receiptHandle) {
    List<SqsMessage> remainingMessages = new ArrayList<>();  
    messages.stream()
        .filter(message -> !StringUtils.equals(receiptHandle,message.getReceiptHandle())).forEach(remainingMessages::add);
    messages.clear();
    messages.addAll(remainingMessages);
  }
}
