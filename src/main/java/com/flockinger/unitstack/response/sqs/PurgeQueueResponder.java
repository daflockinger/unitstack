package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class PurgeQueueResponder extends SqsResponder {

  private final static String PURGE_QUEUE_ACTION = "PurgeQueue";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(PURGE_QUEUE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = extractQueueName(request);
    if(request.getQueues().containsKey(queueName)) {
      request.getQueues().get(queueName).purge();
    }
    return new MockResponse(request.utils().successBody(PURGE_QUEUE_ACTION, null));
  }

}
