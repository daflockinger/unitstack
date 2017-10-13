package com.flockinger.unitstack.response.s3;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.DefaultResponder;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class DefaultS3Responder extends DefaultResponder {
  @Override
  public MockResponse createResponse(MockRequest request) {
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    
    if(StringUtils.equals(action,"requestPayment") ) {
      return new MockResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<RequestPaymentConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n" + 
          "  <Payer>Requester</Payer></RequestPaymentConfiguration>");
    } 
    if(StringUtils.endsWith(action, "tagging")) {
      return new MockResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + 
          "<Tagging xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n" + 
          "<TagSet></TagSet></Tagging> ");
    }
    if(StringUtils.isEmpty(action)) {
      return new MockResponse("");
    }
    return super.createResponse(request);
  }
}
