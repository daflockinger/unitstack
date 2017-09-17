package com.flockinger.unitstack.response.sqs;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.Responder;

abstract class SqsResponder implements Responder {
  public abstract boolean isSameAction(MockRequest request);
  public abstract MockResponse createResponse(MockRequest request);
  
}
