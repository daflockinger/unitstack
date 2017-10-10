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
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

public class PutObjectResponder extends S3Responder {

  private final static String MD5_HEADER_NAME = "Content-MD5";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);

    return StringUtils.equals("PUT", method) && request.getFileContent() != null && !StringUtils.contains(xml, "AccessControlPolicy")
        && StringUtils.isNotEmpty(xml) && getObjectKey(request).isPresent();
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String bucketName = getBucketFromUrl(request);
    Optional<String> key = getObjectKey(request);
    byte[] fileContent = request.utils().unchunkResponse(request.getFileContent());
    
    if(request.getBuckets().containsKey(bucketName) && fileContent != null) {
      Bucket bucket = request.getBuckets().get(bucketName);
      S3Object s3Object = new S3Object();
      s3Object.setKey(key.get());
      s3Object.setObjectData(fileContent);
      s3Object.setMd5(request.getBodyParameters().get(MD5_HEADER_NAME));
      bucket.getObjects().add(s3Object);
    }
    return new MockResponse("");
  }
}

