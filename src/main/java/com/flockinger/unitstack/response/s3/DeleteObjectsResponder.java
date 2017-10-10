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
