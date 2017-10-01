package com.flockinger.unitstack.response.s3;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class BucketExistsResponder extends S3Responder {

  @Override
  public MockResponse createResponse(MockRequest request) {
    String bucketName = getBucketFromUrl(request);
    int responseStatus = 404;
    
    if(request.getBuckets().containsKey(bucketName)) {
      responseStatus = 200;
    }
    return new MockResponse(responseStatus,"<AccessControlPolicy>\n" + 
        "  <AccessControlList></AccessControlList>\n" + 
        "</AccessControlPolicy>");
  }
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return false;//StringUtils.equals(method, "GET") && StringUtils.equals(action, "acl");
  }
}
