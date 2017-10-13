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

import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.S3Part;
import com.flockinger.unitstack.transformer.S3RequestTransformer;


public class PutObjectResponder extends S3Responder {

  private final static String MD5_HEADER_NAME = "Content-MD5";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String xml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);

    return StringUtils.equals("PUT", method) && request.getFileContent() != null && !StringUtils.contains(xml, "AccessControlPolicy")
        && StringUtils.isNotEmpty(xml) && getObjectKey(request).isPresent() && !StringUtils.endsWith(url,"?tagging");
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    Optional<String> key = getObjectKey(request);
    
    if(isPartUploadRequest(request)) {
      createPart(request, bucket, key);
    } else {
      createObject(request,bucket,key.get());
    }
    return new MockResponse("");
  }
  
  private boolean isPartUploadRequest(MockRequest request) {
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);
    return StringUtils.contains(url, "uploadId=");
  }
  
  private void createPart(MockRequest request, Optional<Bucket> bucket, Optional<String> key) {
    byte[] fileContent = request.utils().unchunkResponse(request.getFileContent());
    Map<String,String> queryParams = request.utils().queryStringToMap(request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME));
    String partNumber = queryParams.get("partNumber");
    S3Object object = null;
    if(bucket.isPresent()) {
      object = bucket.get().getObjects().stream().filter(s3obj -> s3obj.getKey().equals(key.get())).findAny().orElse(null);
    }
    if(object != null) {
      object.getParts().add(new S3Part(fileContent, key.get() +"."+ partNumber, queryParams.get("uploadId")));
    }
  }

  private void createObject(MockRequest request, Optional<Bucket> bucket, String key) {
    byte[] fileContent = request.utils().unchunkResponse(request.getFileContent());
    
    if(bucket.isPresent() && fileContent != null) {
      S3Object s3Object = new S3Object();
      s3Object.setKey(key);
      s3Object.setObjectData(fileContent);
      s3Object.setMd5(request.getBodyParameters().get(MD5_HEADER_NAME));
      bucket.get().getObjects().add(s3Object);
    }
  }
}

