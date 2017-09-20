package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class DeleteQueueResponder extends SqsResponder {

  private final static String DELETE_QUEUE_ACTION = "DeleteQueue";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(DELETE_QUEUE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    if(request.getQueues().containsKey(queueName)) {
      request.getQueues().remove(queueName);
    }
    return new MockResponse(request.utils().successBody(DELETE_QUEUE_ACTION, null));
  }
}
