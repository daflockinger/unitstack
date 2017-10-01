package com.flockinger.unitstack.response.s3;

import java.util.Date;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

public class CreateBucketResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String xmlBody = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    return method.equals("PUT") && StringUtils.isEmpty(xmlBody);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String bucketName = getBucketFromUrl(request);
    
    if(StringUtils.isNotEmpty(bucketName)) {
      Bucket bucket = new Bucket();
      bucket.setCreated(new Date());
      bucket.setName(bucketName);
      request.getBuckets().put(bucketName, bucket);
    }
    return new MockResponse("");
  }
}
