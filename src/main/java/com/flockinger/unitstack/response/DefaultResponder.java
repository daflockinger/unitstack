package com.flockinger.unitstack.response;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class DefaultResponder implements Responder {
  
  @Override
  public MockResponse createResponse(MockRequest request) {
    String action = request.utils().getAction(request.getBodyParameters());
    
    if(request.getMockParameters().isRequestSuccessfull()) {
      return new MockResponse(request.utils().successBody(action, null));
    } else {
      return new MockResponse(400, request.utils().errorBody(request.getMockParameters()));
    }
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    return true;
  }
}
