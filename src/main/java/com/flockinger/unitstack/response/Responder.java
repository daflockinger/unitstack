package com.flockinger.unitstack.response;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public interface Responder {
  MockResponse createResponse(MockRequest request);
  
  default boolean isSameAction(MockRequest request) {
    return false;
  }
}
