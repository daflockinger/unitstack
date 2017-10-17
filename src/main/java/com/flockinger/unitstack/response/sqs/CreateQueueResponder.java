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

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;


public class CreateQueueResponder extends SqsResponder {

  private final static String CREATE_QUEUE_ACTION = "CreateQueue";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(CREATE_QUEUE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = request.getBodyParameters().get("QueueName");
    String url = "";

    if (StringUtils.isNotEmpty(queueName)) {
      url = UnitStackTest.UNIT_STACK_URL + ":" + UnitStackTest.SQS_PORT + "/123456789012/"
          + queueName;
      AwsQueue queue = new AwsQueue(queueName, url);
      request.getQueues().put(queueName, queue);
    }
    return new MockResponse(
        request.utils().successBody(CREATE_QUEUE_ACTION, "<QueueUrl>" + url + "</QueueUrl>"));
  }

}
