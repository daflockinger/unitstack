package com.flockinger.unitstack.response.sqs;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.model.sqs.BatchEntry;
import com.flockinger.unitstack.model.sqs.SqsMessage;
import com.google.common.collect.ImmutableList;

public class DeleteMessageBatchResponder extends SqsResponder {

  private final static String DELETE_MESSAGE_BATCH_ACTION = "DeleteMessageBatch";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(DELETE_MESSAGE_BATCH_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    List<BatchEntry> batchEntries = extractBatchEntries(request,"DeleteMessageBatchRequestEntry.");
    String content = null;
    if(request.getQueues().containsKey(queueName)) {
      removeBatchFromQueue(request.getQueues().get(queueName),batchEntries);
      content = batchEntries.stream().map(this::createDeleteBatchResultXml).collect(Collectors.joining("\n"));
    }
    return new MockResponse(request.utils().successBody(DELETE_MESSAGE_BATCH_ACTION, content));
  }
  
  private void removeBatchFromQueue(AwsQueue queue, List<BatchEntry> batchEntries) {
    removeBatchEntriesFromQueue(batchEntries,queue.getMessageQueue());
    for(BatchEntry batchEntry : batchEntries) {
      removeBatchEntriesFromQueue(ImmutableList.of(batchEntry),queue.getInvisibilityQueueFor(batchEntry.getReceiptHandle()));
    }
  }
  
  private void removeBatchEntriesFromQueue(List<BatchEntry> batchEntries, Queue<SqsMessage> messages) {
    List<SqsMessage> remainingMessages = new ArrayList<>();  
    messages.stream()
        .filter(message -> isMessageNotOnDeletionList(message,batchEntries)).forEach(remainingMessages::add);
    messages.clear();
    messages.addAll(remainingMessages);
  }
  
  private boolean isMessageNotOnDeletionList(SqsMessage message, List<BatchEntry> deletionList) {
    return ! deletionList.stream().anyMatch(entry -> 
    StringUtils.equals(entry.getId(), message.getBatchId()) &&
    StringUtils.equals(entry.getReceiptHandle(), message.getReceiptHandle()));
  }
  private String createDeleteBatchResultXml(BatchEntry message) {
    return "<DeleteMessageBatchResultEntry>" + 
        "      <Id>"+message.getId()+"</Id>" + 
        "   </DeleteMessageBatchResultEntry>";
  }
}
