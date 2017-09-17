package com.flockinger.unitstack.response.sns;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;

import wiremock.org.apache.commons.lang3.StringUtils;

public class GetSubscriptionAttributesResponder extends SnsResponder {

  private final static String GET_SUB_ATTR_ACTION = "GetSubscriptionAttributes";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(GET_SUB_ATTR_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String subscriptionArn = request.getBodyParameters().get("SubscriptionArn");
    Optional<Subscription> subscription = findSubscriptionWithArn(subscriptionArn,request.getTopics().values());
    String content = "<Attributes></Attributes>";
   
    if(subscription.isPresent()) {
      content = getAttributeResponseXml(subscription.get().getAttributes());
    }     
    return new MockResponse(request.utils().successBody(GET_SUB_ATTR_ACTION,content));
  }
}
