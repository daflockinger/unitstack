package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;

public class GetQueueUrlResponder extends SqsResponder {

  private final static String GET_QUEUE_URL_ACTION = "GetQueueUrl";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(GET_QUEUE_URL_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = request.getBodyParameters().get("QueueName");
    String url = "";
    
    if(request.getQueues().containsKey(queueName)) {
     AwsQueue queue = request.getQueues().get(queueName);
     url = queue.getUrl();
    }
    return new MockResponse(request.utils().successBody(GET_QUEUE_URL_ACTION, "<QueueUrl>" + url + "</QueueUrl>"));
  }

}
