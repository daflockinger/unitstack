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
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Action;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.dto.ListBucketResult;
import com.flockinger.unitstack.model.s3.dto.ListMultipartResult;
import com.flockinger.unitstack.model.s3.dto.ObjectSummary;
import com.flockinger.unitstack.model.s3.dto.Owner;
import com.flockinger.unitstack.model.s3.dto.PartUploadResult;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class ListObjectsResponder extends S3Responder {

  public final static int MAX_KEYS = 10;
  private final static int MORE_THAN_ENOUGH = 1000000;
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return S3ActionInvestigator.get().isAction(request, S3Action.LIST_OBJECTS);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucket(request);
    Object result = null;
    
    if(isPartUploadRequest(request)) {
      result = getPartsResult(request,bucket);
    } else {
      result = getObjectResult(request,bucket);
    }
    return new MockResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + request.utils().toXmlString(result));
  }
  
  private boolean isPartUploadRequest(MockRequest request) {
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.contains(action, "uploads&");
  }
  
  private ListMultipartResult getPartsResult(MockRequest request, Optional<Bucket> bucket) {
    ListMultipartResult result = new ListMultipartResult();
    if(bucket.isPresent()) {
      result = new ListMultipartResult(bucket.get().getName(), request);
      result.setUploads(getParts(bucket.get(),result));
    }
    return result;
  }

  private List<PartUploadResult> getParts(Bucket bucket, ListMultipartResult result) {
    List<S3Object> filteredObjects = filterS3Objects(bucket.getObjects(),null,result.getPrefix(),MORE_THAN_ENOUGH);
    return filteredObjects.stream()
        .flatMap(s3obj -> s3obj.getParts().stream())
        .map(part -> new PartUploadResult(part.getKey(),part.getUploadId(),getNobodyOwner()))
        .limit(result.getMaxUploads())
        .collect(Collectors.toList());
  }
  
  private ListBucketResult getObjectResult(MockRequest request, Optional<Bucket> bucket) { 
    ListBucketResult result = new ListBucketResult();
    
    if(bucket.isPresent()) {
      result = new ListBucketResult(bucket.get().getName(), request);
      result.setContents(filterS3Objects(bucket.get().getObjects(), 
          result.getMarker(), result.getPrefix(), result.getMaxKeys())
          .stream().map(s3Object -> new ObjectSummary(s3Object.getKey(), s3Object.getObjectData().length, getNobodyOwner()))
          .collect(Collectors.toList()));
      result.setIsTruncated(result.getContents().size() != bucket.get().getObjects().size());
    }
    return result;
  }
  
  private List<S3Object> filterS3Objects(List<S3Object> objects, String marker, String prefix, int maxObjects) {
    int skip = 0;
    if(StringUtils.isNotEmpty(marker)) {
      skip = IntStream.range(0, objects.size())
          .filter(index ->  !StringUtils.equals(objects.get(index).getKey(),marker))
          .findFirst().orElse(0);
    }
    return objects.stream()
          .filter(s3Object -> hasObjectKeyPrefixIfExists(prefix,s3Object))
          .skip(skip).limit(maxObjects)
          .collect(Collectors.toList());
  }
  
  private boolean hasObjectKeyPrefixIfExists(String prefix, S3Object s3Object) { 
    return StringUtils.isNotEmpty(prefix) ? StringUtils.startsWith(s3Object.getKey(), prefix) : true;
  }
  
  private Owner getNobodyOwner() {
    Owner fakeOwner = new Owner();
    fakeOwner.setDisplayName("nobody");
    fakeOwner.setId(UUID.randomUUID().toString());
    return fakeOwner;
  }
}
