package com.flockinger.unitstack.sns.response.impl;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

import wiremock.org.apache.commons.lang3.StringUtils;

public class GetSubscriptionAttributesResponder extends SnsResponder {

  private final static String GET_SUB_ATTR_ACTION = "GetSubscriptionAttributes";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return GET_SUB_ATTR_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String subscriptionArn = request.getBodyParameters().get("SubscriptionArn");
    Optional<Subscription> subscription = findSubscriptionWithArn(subscriptionArn,request.getTopics().values());
    String content = "<Attributes></Attributes>";
   
    if(subscription.isPresent()) {
      content = getAttributeResponseXml(subscription.get().getAttributes());
    }     
    return new MockResponse(successBody(GET_SUB_ATTR_ACTION,content));
  }
}
