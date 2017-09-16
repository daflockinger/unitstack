package com.flockinger.unitstack.sns.response.impl;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class SetSubscriptionAttributesResponder extends SnsResponder {

  private final static String SET_SUB_ATTR_ACTION = "SetSubscriptionAttributes";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return SET_SUB_ATTR_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String subscriptionArn = request.getBodyParameters().get("SubscriptionArn");
    String attributeName = request.getBodyParameters().get("AttributeName");
    String attributeValue = request.getBodyParameters().get("AttributeValue");
    Optional<Subscription> subscription = findSubscriptionWithArn(subscriptionArn,request.getTopics().values());
    
    if(subscription.isPresent()) {
      subscription.get().getAttributes().put(attributeName, attributeValue);
    }
    return new MockResponse(successBody(SET_SUB_ATTR_ACTION, null));
  }
}
