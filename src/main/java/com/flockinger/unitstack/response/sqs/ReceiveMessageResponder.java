package com.flockinger.unitstack.response.sqs;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.math.NumberUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.model.sqs.SqsMessage;


public class ReceiveMessageResponder extends SqsResponder {

  private final static String RECEIVE_MESSAGE_ACTION = "ReceiveMessage";
  private final static int DEFAULT_FETCH_SIZE = 1;
  // private final static int DEFAULT_VISIBILITY_TIMEOUT = 30;

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(RECEIVE_MESSAGE_ACTION, request);
  }

  // TODO simulate real visibility timeout behaviour
  @Override
  public MockResponse createResponse(MockRequest request) {
    int maxNumberOfMessages = NumberUtils
        .toInt(request.getBodyParameters().get("MaxNumberOfMessages"), DEFAULT_FETCH_SIZE);
    // int visibilityTimeout = NumberUtils.toInt(request.getBodyParameters().get("VisibilityTimeout"),DEFAULT_VISIBILITY_TIMEOUT);
    String receiptHandle = UUID.randomUUID().toString();
    String queueName = extractQueueName(request);
    List<SqsMessage> messages = new ArrayList<>();
    
    if (request.getQueues().containsKey(queueName)) {
      AwsQueue queue = request.getQueues().get(queueName);
      messages = pollMaxMessages(maxNumberOfMessages, queue, receiptHandle);
    } 
    String messageResponses = messages.stream().map(this::getMessageResponseXml).collect(Collectors.joining("\n"));
    return new MockResponse(request.utils().successBody(RECEIVE_MESSAGE_ACTION, messageResponses));
  }
  
  
  private String getMessageResponseXml(SqsMessage message) {
    return "<Message> <MessageId>" + message.getId() + "</MessageId>" + 
        "      <ReceiptHandle>" + message.getReceiptHandle() + "</ReceiptHandle>" + 
        "      <MD5OfBody>" + message.getMd5() + "</MD5OfBody>" + 
        "      <Body>" + message.getBody() + "</Body> </Message>";
  }

  private List<SqsMessage> pollMaxMessages(int maxAmount, AwsQueue queue, String receiptHandle) {
    List<SqsMessage> messages = new ArrayList<>();
    for (int messageCount = 0; (messageCount < maxAmount
        && queue.getMessageQueue().peek() != null); messageCount++) {
      SqsMessage message = queue.getMessageQueue().poll();
      message.setReceiptHandle(receiptHandle);
      messages.add(message);
    }
    queue.getInvisibilityQueueFor(receiptHandle).addAll(messages);
    return messages;
  }
}
