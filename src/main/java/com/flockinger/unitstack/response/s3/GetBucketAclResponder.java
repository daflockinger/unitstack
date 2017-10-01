package com.flockinger.unitstack.response.s3;

import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Grant;
import com.flockinger.unitstack.model.s3.dto.AccessControlPolicy;
import com.flockinger.unitstack.model.s3.dto.Owner;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class GetBucketAclResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.equals(method, "GET") && StringUtils.equals(action, "acl");
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String bucketName = getBucketFromUrl(request);
    String content = request.utils().toXmlString(new AccessControlPolicy());
    int responseStatus = 404;
    
    if(request.getBuckets().containsKey(bucketName)) {
      responseStatus = 200;
      content = getAclPolicyXml(request,bucketName);
    }
    return new MockResponse(responseStatus,content);
  }

  private String getAclPolicyXml(MockRequest request, String bucketName) {
    AccessControlPolicy policy = new AccessControlPolicy();
    List<Grant> grants = request.getBuckets().get(bucketName).getAccessControllList();
    policy.setAccessControllList(grants);
    Owner owner = new Owner();
    owner.setId(UUID.randomUUID().toString());
    owner.setDisplayName("");
    policy.setOwner(owner);
    
    return request.utils().toXmlString(policy);
  }
}
