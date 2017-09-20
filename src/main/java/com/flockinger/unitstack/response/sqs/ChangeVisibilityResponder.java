package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class ChangeVisibilityResponder extends SqsResponder {

  private final static String CHANGE_VISIBILITY_ACTION = "ChangeMessageVisibility";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(CHANGE_VISIBILITY_ACTION, request);
  }

  //TODO do something with it
  @Override
  public MockResponse createResponse(MockRequest request) {
     // int visibilityTimeout = NumberUtils.toInt(request.getBodyParameters().get("VisibilityTimeout"),DEFAULT_VISIBILITY_TIMEOUT); 
    return new MockResponse(request.utils().successBody(CHANGE_VISIBILITY_ACTION, null));
  }

}
