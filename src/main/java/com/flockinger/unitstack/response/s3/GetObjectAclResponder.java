package com.flockinger.unitstack.response.s3;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.Grant;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.dto.AccessControlPolicy;
import com.flockinger.unitstack.model.s3.dto.Owner;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class GetObjectAclResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.equals(method, "GET") && StringUtils.equals(action, "acl") && getObjectKey(request).isPresent();
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    Optional<S3Object> object = Optional.empty();  
    String content = "";
    int responseStatus = 404;
    
    if (bucket.isPresent()) {
      object = getObject(bucket.get(), getObjectKey(request).get());
    }
    if(object.isPresent()) {
      responseStatus = 200;
      content = getAclPolicyXml(request,object.get());
    }
    return new MockResponse(responseStatus,content);
  }

  private String getAclPolicyXml(MockRequest request, S3Object object) {
    AccessControlPolicy policy = new AccessControlPolicy();
    List<Grant> grants = object.getAccessControllList();
    policy.setAccessControllList(grants);
    Owner owner = new Owner();
    owner.setId(UUID.randomUUID().toString());
    owner.setDisplayName("");
    policy.setOwner(owner);
    
    return request.utils().toXmlString(policy);
  }
}
