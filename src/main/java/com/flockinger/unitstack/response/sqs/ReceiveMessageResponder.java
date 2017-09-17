package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class ReceiveMessageResponder extends SqsResponder{

  private final static String RECEIVE_MESSAGE_ACTION = "ReceiveMessage";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(RECEIVE_MESSAGE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    // TODO Auto-generated method stub
    return null;
  }

}
