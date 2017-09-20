package com.flockinger.unitstack.response;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class DefaultResponder implements Responder {
  
  @Override
  public MockResponse createResponse(MockRequest request) {
    String action = request.utils().getAction(request.getBodyParameters());
    
    if(shouldRequestBeSuccessfull(request)) {
      return new MockResponse(request.utils().successBody(action, null));
    } else {
      return new MockResponse(400, request.utils().errorBody(request.getMockParameters()));
    }
  }
  
  private boolean shouldRequestBeSuccessfull(MockRequest request) {
    return request.getMockParameters() == null || request.getMockParameters().isRequestSuccessfull();
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    return true;
  }
}
