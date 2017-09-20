package com.flockinger.unitstack.response.sns;

import java.util.Optional;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;

public class ConfirmSubscriptionResponder extends SnsResponder {

  private final static String CONFIRM_SUB_ACTION = "ConfirmSubscription";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(CONFIRM_SUB_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String content = null;
    Optional<Subscription> maybeSub = Optional.empty();
    
    if(request.getTopics().containsKey(topicArn)) {
       maybeSub = request.getTopics().get(topicArn).getSubscriptions().stream().findFirst();
    }
    if(maybeSub.isPresent()) {
      content = "<SubscriptionArn>" + maybeSub.get().getSubscriptionArn() + "</SubscriptionArn>";
    }
    return new MockResponse(request.utils().successBody(CONFIRM_SUB_ACTION, content));
  }

}
