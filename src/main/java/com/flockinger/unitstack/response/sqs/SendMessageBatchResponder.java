/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
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
    List<SqsMessage> messages = extractBatchEntries(request, "SendMessageBatchRequestEntry.")
        .stream().map(batchEntry -> createMessageFrom(batchEntry, receiptHandle, request.utils()))
        .collect(toList());
    String content = null;

    if (request.getQueues().containsKey(queueName)) {
      request.getQueues().get(queueName).getMessageQueue().addAll(messages);
      content = messages.stream().map(this::getBatchResultEntryXml).collect(joining("\n"));
    }
    return new MockResponse(request.utils().successBody(SEND_MESSAGE_BATCH_ACTION, content));
  }

  private SqsMessage createMessageFrom(BatchEntry batchEntry, String receiptHandle,
      MessageUtils utils) {
    SqsMessage message = new SqsMessage();
    message.setBody(batchEntry.getBody());
    message.setId(UUID.randomUUID().toString());
    message.setBatchId(batchEntry.getId());
    message.setReceiptHandle(receiptHandle);
    message.setMd5(utils.getMD5(batchEntry.getBody()));
    return message;
  }

  private String getBatchResultEntryXml(SqsMessage message) {
    return "<SendMessageBatchResultEntry>" + "  <Id>" + message.getBatchId() + "</Id>"
        + "  <MessageId>" + message.getId() + "</MessageId>" + "  <MD5OfMessageBody>"
        + message.getMd5() + "</MD5OfMessageBody>" + "</SendMessageBatchResultEntry> ";
  }
}
