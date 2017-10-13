/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
    return StringUtils.equals(method, "GET") && StringUtils.equals(action, "acl") && !getObjectKey(request).isPresent();
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
