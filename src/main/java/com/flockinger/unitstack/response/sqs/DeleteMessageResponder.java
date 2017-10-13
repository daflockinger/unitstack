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
