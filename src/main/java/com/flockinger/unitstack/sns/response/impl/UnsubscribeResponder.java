package com.flockinger.unitstack.sns.response.impl;

import static java.util.stream.Collectors.toList;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class UnsubscribeResponder extends SnsResponder {

  private final static String UNSUBSCRIBE_ACTION = "Unsubscribe";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return UNSUBSCRIBE_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String subscriptionArn = request.getBodyParameters().get("SubscriptionArn");
    
    Optional<Topic> maybeTopic = findTopicWithSubscription(subscriptionArn,request.getTopics().values());
    if(maybeTopic.isPresent()) {
      maybeTopic.get().setSubscriptions(maybeTopic.get().getSubscriptions().stream()
          .filter(sub -> !hasSubscriptionArn(subscriptionArn,sub)).collect(toList()));
    }
    return new MockResponse(successBody(UNSUBSCRIBE_ACTION, null));
  }
}
