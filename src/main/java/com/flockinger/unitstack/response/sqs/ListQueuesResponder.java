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
    List<String> queueUrls = request.getQueues().values().stream().map(AwsQueue::getUrl).collect(Collectors.toList());
    
    if(queueNamePrefix != null) {
      queueUrls =request.getQueues().values().stream()
          .filter(queue -> StringUtils.startsWith(queue.getName(), queueNamePrefix))
          .map(AwsQueue::getUrl).collect(Collectors.toList());
    }
    return new MockResponse(request.utils()
        .successBody(LIST_QUEUES_ACTION, getQueueListXml(queueUrls)));
  }
  
  private String getQueueListXml(List<String> queueUrls) {
    return queueUrls.stream()
        .map(url -> "<QueueUrl>" + url + "</QueueUrl>")
        .collect(Collectors.joining(""));
  }
}
