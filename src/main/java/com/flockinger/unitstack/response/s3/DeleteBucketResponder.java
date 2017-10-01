package com.flockinger.unitstack.response.s3;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

public class DeleteBucketResponder extends S3Responder {

  @Override
  public MockResponse createResponse(MockRequest request) {
    String bucketName = getBucketFromUrl(request);
    
    if(request.getBuckets().containsKey(bucketName)) {
      request.getBuckets().remove(bucketName);
    }
    return new MockResponse("");
  }
  
  
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    String bucketName = getBucketFromUrl(request);
    return StringUtils.equals(method, "DELETE") && StringUtils.isNotEmpty(bucketName) && StringUtils.isEmpty(xml);
  }
}
