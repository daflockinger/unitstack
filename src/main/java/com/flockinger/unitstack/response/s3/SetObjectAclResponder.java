package com.flockinger.unitstack.response.s3;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.dto.AccessControlPolicy;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class SetObjectAclResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    String bucketName = getBucketFromUrl(request);

    return StringUtils.equals(method, "PUT") && StringUtils.contains(xml, "AccessControlPolicy")
        && StringUtils.isNotEmpty(bucketName) && getObjectKey(request).isPresent();
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    Optional<S3Object> object = Optional.empty();    
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    Optional<AccessControlPolicy> policy =
        request.utils().fromXmlTo(xml, AccessControlPolicy.class);

    if (bucket.isPresent()) {
      object = getObject(bucket.get(), getObjectKey(request).get());
    }
    if(object.isPresent() && policy.isPresent()) {
      object.get().setAccessControllList(policy.get().getAccessControllList());
    }
    return new MockResponse("");
  }

}
