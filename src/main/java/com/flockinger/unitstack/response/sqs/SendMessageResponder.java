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
    String messageBody =
        request.utils().decodeValue(request.getBodyParameters().get("MessageBody"));
    SqsMessage message = new SqsMessage();
    message.setBody(messageBody);
    message.setMd5(request.utils().getMD5(messageBody));
    message.setId(UUID.randomUUID().toString());

    if (request.getQueues().containsKey(queueName)) {
      AwsQueue queue = request.getQueues().get(queueName);
      queue.getMessageQueue().add(message);
    }
    return new MockResponse(
        request.utils().successBody(SEND_MESSAGE_ACTION, getSendResponseXml(message)));
  }

  private String getSendResponseXml(SqsMessage message) {
    return "<MD5OfMessageBody>" + message.getMd5() + "</MD5OfMessageBody>" + "<MessageId>"
        + message.getId() + "</MessageId>";
  }
}
