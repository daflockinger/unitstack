/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.response.s3;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.S3Action;
import com.flockinger.unitstack.model.s3.dto.AccessControlPolicy;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class SetBucketAclResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    return S3ActionInvestigator.get().isAction(request, S3Action.SET_BUCKET_ACL);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    String bucketName = getBucketName(request);
    Optional<AccessControlPolicy> policy =
        request.utils().fromXmlTo(xml, AccessControlPolicy.class);

    if (policy.isPresent() && request.getBuckets().containsKey(bucketName)) {
      request.getBuckets().get(bucketName)
          .setAccessControllList(policy.get().getAccessControllList());
    }
    return new MockResponse("");
  }
}
