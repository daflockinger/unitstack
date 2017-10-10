package com.flockinger.unitstack.response.s3;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.model.s3.dto.ListBucketResult;
import com.flockinger.unitstack.model.s3.dto.ObjectSummary;
import com.flockinger.unitstack.model.s3.dto.Owner;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class ListObjectsResponder extends S3Responder {

  private final static int MAX_KEYS = 10;
  
  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    return StringUtils.equals(method, "GET") && StringUtils.containsAny(action, "prefix=","encoding-type=",
        "delimiter=","list-type=","max-keys=");
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    ListBucketResult result = getResult(request);
    String content = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
    if(result == null) {
      result = new ListBucketResult(null, null, null,null);
    }
    content += request.utils().toXmlString(result);
    
    return new MockResponse(content);
  }
  
  
  private ListBucketResult getResult(MockRequest request) {
    Optional<Bucket> bucket = getBucketFromRequest(request);
    Map<String,String> listParameters = request.utils().queryStringToMap(request.getBodyParameters().get(S3RequestTransformer.ACTION));
    String prefix = listParameters.get("prefix");
    int maxKeys = NumberUtils.toInt(listParameters.get("max-keys"), MAX_KEYS);
    String marker = listParameters.get("marker");
    ListBucketResult result = null;
    
    if(bucket.isPresent()) {
      result = new ListBucketResult(bucket.get().getName(), prefix, maxKeys, marker);
      result.setContents(getSummaries(bucket.get().getObjects(), result));
    }
    return result;
  }
  
  private List<ObjectSummary> getSummaries(List<S3Object> objects, ListBucketResult result) {  
    int skip = 0;
    String marker = result.getMarker();
    if(StringUtils.isNotEmpty(marker)) {
      skip = IntStream.range(0, objects.size())
          .filter(index ->  !StringUtils.equals(objects.get(index).getKey(),marker))
          .findFirst()
          .orElse(0);
    }
    return objects.stream()
          .filter(s3Object -> hasObjectKeyPrefixIfExists(result.getPrefix(),s3Object))
          .skip(skip)
          .limit(result.getMaxKeys())
          .map(this::map)
          .collect(Collectors.toList());
  }
  
  private boolean hasObjectKeyPrefixIfExists(String prefix, S3Object s3Object) { 
    if(StringUtils.isNotEmpty(prefix)) {
      return StringUtils.startsWith(s3Object.getKey(), prefix);
    }
    return true;
  }
  
  private ObjectSummary map(S3Object s3Object) {
    Owner fakeOwner = new Owner();
    fakeOwner.setDisplayName("nobody");
    fakeOwner.setId(UUID.randomUUID().toString());
    return new ObjectSummary(s3Object.getKey(), s3Object.getObjectData().length, fakeOwner);
  }  
}
