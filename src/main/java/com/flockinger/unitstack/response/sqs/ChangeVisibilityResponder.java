package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class ChangeVisibilityResponder extends SqsResponder {

  private final static String CHANGE_VISIBILITY_ACTION = "ChangeMessageVisibility";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(CHANGE_VISIBILITY_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
