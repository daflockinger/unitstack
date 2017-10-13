/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
