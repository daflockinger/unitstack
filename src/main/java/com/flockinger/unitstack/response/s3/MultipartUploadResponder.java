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

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.S3Part;
import com.flockinger.unitstack.model.s3.dto.MultipartResult;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

import wiremock.org.apache.commons.lang3.ArrayUtils;

public class MultipartUploadResponder extends S3Responder {

  public final static String MULTIPART_OP_NAME_PLACEHOLDER = "__MultipartUpload";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);

    return StringUtils.equals(method, "POST") && StringUtils.containsAny(url,"?uploads","uploadId=") && getObjectKey(request).isPresent();
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    Optional<String> key = getObjectKey(request);
    MultipartResult result = new MultipartResult();
    String action = "InitiateMultipartUpload";
    
    if(isActionInitialize(request)) {
      result = initPartUpload(bucket, key.get());
    } else {
      action = "CompleteMultipartUpload";
      result = completePartUpload(bucket, key);
    }
    
    return new MockResponse(getResultXml(result,action,request));
  }
  
  private boolean isActionInitialize(MockRequest request) {
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);
    return StringUtils.contains(url,"?uploads");
  }
  
  
  private MultipartResult initPartUpload(Optional<Bucket> bucket, String key) {
    MultipartResult result = new MultipartResult();
    if(bucket.isPresent() && StringUtils.isNotEmpty(key)) {
      S3Object s3Object = new S3Object();
      s3Object.setKey(key);
      bucket.get().getObjects().add(s3Object);
      result = new MultipartResult(bucket.get().getName(),key);
    }
    return result;
  }
  
  private MultipartResult completePartUpload(Optional<Bucket> bucket, Optional<String> key) {
    MultipartResult result = new MultipartResult();
    Optional<S3Object> object = Optional.empty();
    if(bucket.isPresent() && key.isPresent()) {
      object = bucket.get().getObjects().stream().filter(obj -> StringUtils.equals(obj.getKey(),key.get())).findFirst();
      result = new MultipartResult(bucket.get().getName(),key.get());
    }
    if(object.isPresent()) {
      object.get().setObjectData(assembleParts(object.get().getParts()));
      object.get().getParts().clear();
    }
    return result;
  }
  
  private byte[] assembleParts(List<S3Part> parts) {
    List<Byte> assembledBytes = parts.stream().map(part -> Arrays.asList(ArrayUtils.toObject(part.getPartBytes())))
    .flatMap(List::stream)
    .collect(Collectors.toList());
    
    return ArrayUtils.toPrimitive(assembledBytes.toArray(new Byte[assembledBytes.size()]));
  }
  
  private String getResultXml(MultipartResult result, String action, MockRequest request) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
  request.utils().toXmlString(result).replaceAll(MULTIPART_OP_NAME_PLACEHOLDER, action);
  }

}
