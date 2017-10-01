package com.flockinger.unitstack.response.s3;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

public class GetBucketLocationResponder extends S3Responder {

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<String> region = getCustomRegion(request);
    String content = "";

    if (region.isPresent()) {
      content = "<LocationConstraint>" + region.get() + "</LocationConstraint>";
    }
    return new MockResponse(content);
  }

  private Optional<String> getCustomRegion(MockRequest request) {
    Optional<String> region = Optional.empty();

    if (request.getMockParameters() != null) {
      region = Optional.ofNullable(request.getMockParameters().getMockRegion());
    }
    return region;
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    String bucketName = getBucketFromUrl(request);

    return StringUtils.equals(method, "GET") && StringUtils.equals(action, "location")
        && StringUtils.isNotEmpty(bucketName);
  }
}
