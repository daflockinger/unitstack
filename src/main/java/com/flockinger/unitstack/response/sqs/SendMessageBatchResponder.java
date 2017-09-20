package com.flockinger.unitstack.response.sqs;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.BatchEntry;
import com.flockinger.unitstack.model.sqs.SqsMessage;
import com.flockinger.unitstack.utils.MessageUtils;

public class SendMessageBatchResponder extends SqsResponder {

  private final static String SEND_MESSAGE_BATCH_ACTION = "SendMessageBatch";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(SEND_MESSAGE_BATCH_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    String receiptHandle = UUID.randomUUID().toString();
    List<SqsMessage> messages = extractBatchEntries(request,"SendMessageBatchRequestEntry.").stream().map(batchEntry -> 
        createMessageFrom(batchEntry,receiptHandle,request.utils())).collect(toList());
    String content = null;
    
    if(request.getQueues().containsKey(queueName)) {
      request.getQueues().get(queueName)
            .getMessageQueue().addAll(messages);
      content = messages.stream().map(this::getBatchResultEntryXml).collect(joining("\n"));
    }
    return new MockResponse(request.utils().successBody(SEND_MESSAGE_BATCH_ACTION, content));
  }
  
  private SqsMessage createMessageFrom(BatchEntry batchEntry, String receiptHandle, MessageUtils utils) {
    SqsMessage message = new SqsMessage();
    message.setBody(batchEntry.getBody());
    message.setId(UUID.randomUUID().toString());
    message.setBatchId(batchEntry.getId());
    message.setReceiptHandle(receiptHandle);
    message.setMd5(utils.getMD5(batchEntry.getBody()));
    return message;
  }
 
  private String getBatchResultEntryXml(SqsMessage message) {
    return "<SendMessageBatchResultEntry>" + "  <Id>" + message.getBatchId() + "</Id>" + "  <MessageId>"
        + message.getId() + "</MessageId>" + "  <MD5OfMessageBody>" + message.getMd5()
        + "</MD5OfMessageBody>" + "</SendMessageBatchResultEntry> ";
  }
}
