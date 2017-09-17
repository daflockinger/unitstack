package com.flockinger.unitstack.response.sns;

import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;

public class SubscribeResponder extends SnsResponder {

  private final static String SUBSCRIBE_ACTION = "Subscribe";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(SUBSCRIBE_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String protocol = request.getBodyParameters().get("Protocol");
    String endpoint = request.getBodyParameters().get("Endpoint");
    String subscriptionArn = topicArn + ":" +UUID.randomUUID().toString().substring(0,5);
    
    if(request.getTopics().containsKey(topicArn)) {
      Subscription subscription = new Subscription();
      subscription.setEndpoint(endpoint);
      subscription.setProtocol(protocol);
      subscription.setSubscriptionArn(subscriptionArn);
      request.getTopics().get(topicArn).getSubscriptions().add(subscription);
    }
    return new MockResponse(request.utils().successBody(SUBSCRIBE_ACTION,"<SubscriptionArn>" + subscriptionArn + "</SubscriptionArn>"));
  }
}
