package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class DeleteMessageResponder extends SqsResponder {

  private final static String DELETE_MESSAGE_ACTION = "DeleteMessage";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(DELETE_MESSAGE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
