package com.flockinger.unitstack.response.s3;

import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class ListBucketsResponder extends S3Responder {

  private final static String LIST_BUCKETS_ACTION="ListAllMyBuckets";
  
  @Override
  public MockResponse createResponse(MockRequest request) {
    
    String bucketsXml = "";
    
    if(request.getBuckets().size() > 0) {
      bucketsXml = request.getBuckets().values().stream()
          .map(bucket -> getBucketXml(bucket, request))
          .collect(Collectors.joining("\n"));
    }
    String content = " <Owner>\n" + 
        "    <ID>" + UUID.randomUUID().toString() + "</ID>\n" + 
        "    <DisplayName>" + UUID.randomUUID().toString() + "</DisplayName>\n" + 
        "  </Owner><Buckets>" + bucketsXml + "</Buckets>";
    
    return new MockResponse(successBody(LIST_BUCKETS_ACTION, content));
  }
  
  private String getBucketXml(Bucket bucket, MockRequest request) {
    return request.utils().toXmlString(bucket);
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    String method = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_METHOD);
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);
    String xmlBody = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_RESPONSE_XML);
    
    return StringUtils.equalsIgnoreCase(method, "GET") && StringUtils.isEmpty(action) && StringUtils.isEmpty(xmlBody);
  }
}
