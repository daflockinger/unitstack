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


import java.util.List;
import java.util.stream.Collectors;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;

import wiremock.org.apache.commons.lang3.StringUtils;

public class ListQueuesResponder extends SqsResponder {

  private final static String LIST_QUEUES_ACTION = "ListQueues";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(LIST_QUEUES_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueNamePrefix = request.getBodyParameters().get("QueueNamePrefix");
    List<String> queueUrls =
        request.getQueues().values().stream().map(AwsQueue::getUrl).collect(Collectors.toList());

    if (queueNamePrefix != null) {
      queueUrls = request.getQueues().values().stream()
          .filter(queue -> StringUtils.startsWith(queue.getName(), queueNamePrefix))
          .map(AwsQueue::getUrl).collect(Collectors.toList());
    }
    return new MockResponse(
        request.utils().successBody(LIST_QUEUES_ACTION, getQueueListXml(queueUrls)));
  }

  private String getQueueListXml(List<String> queueUrls) {
    return queueUrls.stream().map(url -> "<QueueUrl>" + url + "</QueueUrl>")
        .collect(Collectors.joining(""));
  }
}
