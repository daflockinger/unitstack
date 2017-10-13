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
