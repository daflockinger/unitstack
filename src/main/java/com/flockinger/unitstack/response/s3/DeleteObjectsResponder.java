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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.dto.DeleteObject;
import com.flockinger.unitstack.model.s3.dto.DeletedObject;
import com.flockinger.unitstack.model.s3.dto.ObjectSummary;
import com.flockinger.unitstack.transformer.S3RequestTransformer;
import com.flockinger.unitstack.utils.MessageUtils;

public class DeleteObjectsResponder extends S3Responder {

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);    
    return StringUtils.equals(method, "POST") && url.contains("delete") && url.contains("?");
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    List<String> deleteKeys = getDeleteKeysFromRequest(request);
    
    if(deleteKeys.isEmpty()) {
      deleteKeys = bucket.get().getObjects().stream()
          .map(S3Object::getKey).collect(Collectors.toList());
    }
    if(bucket.isPresent()) {
      Optional<List<String>> wrappedDeleteKeys =  Optional.of(deleteKeys);
      bucket.get().getObjects().removeIf(s3Object -> wrappedDeleteKeys.get().contains(s3Object.getKey()));
    }
    return new MockResponse(successBody("Delete", createDeleteXml(deleteKeys,request.utils())));
  }
  
  private List<String> getDeleteKeysFromRequest(MockRequest request) {
    List<String> deleteKeys = new ArrayList<>();
    String deleteXml = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    Optional<DeleteObject> deleteObject = request.utils().fromXmlTo(deleteXml, DeleteObject.class);
    
    if(deleteObject.isPresent() && deleteObject.get().getObjects() != null) {
      deleteKeys = deleteObject.get().getObjects().stream().map(ObjectSummary::getKey).collect(Collectors.toList());
    }
    return deleteKeys;
  }
  
  private String createDeleteXml(List<String> keys, MessageUtils utils) {
    return keys.stream().map(DeletedObject::new).map(utils::toXmlString).collect(Collectors.joining("\n"));
  }
}
