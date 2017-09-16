package com.flockinger.unitstack.sns.response.impl;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class DefaultSnsResponder extends SnsResponder {
  
  @Override
  public MockResponse createResponse(MockRequest request) {
    String action = getAction(request.getBodyParameters());
    
    if(request.getMockParameters().isRequestSuccessfull()) {
      return new MockResponse(successBody(action, null));
    } else {
      return new MockResponse(400, errorBody(request.getMockParameters()));
    }
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    return true;
  }
}
