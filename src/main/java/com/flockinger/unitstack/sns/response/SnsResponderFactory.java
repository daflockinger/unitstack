package com.flockinger.unitstack.sns.response;

import java.util.ArrayList;
import java.util.List;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.sns.response.impl.ConfirmSubscriptionResponder;
import com.flockinger.unitstack.sns.response.impl.CreateTopicResponder;
import com.flockinger.unitstack.sns.response.impl.DefaultSnsResponder;
import com.flockinger.unitstack.sns.response.impl.DeleteTopicResponder;
import com.flockinger.unitstack.sns.response.impl.GetSubscriptionAttributesResponder;
import com.flockinger.unitstack.sns.response.impl.GetTopicAttributeResponder;
import com.flockinger.unitstack.sns.response.impl.ListSubscriptionsByTopicResponder;
import com.flockinger.unitstack.sns.response.impl.ListSubscriptionsResponder;
import com.flockinger.unitstack.sns.response.impl.ListTopicResponder;
import com.flockinger.unitstack.sns.response.impl.PublishResponder;
import com.flockinger.unitstack.sns.response.impl.SetSubscriptionAttributesResponder;
import com.flockinger.unitstack.sns.response.impl.SetTopicAttributeResponder;
import com.flockinger.unitstack.sns.response.impl.SubscribeResponder;
import com.flockinger.unitstack.sns.response.impl.UnsubscribeResponder;

public class SnsResponderFactory {
  private List<SnsResponder> responders;
  private SnsResponder defaultResponder;
  
  public SnsResponderFactory() {
    defaultResponder = new DefaultSnsResponder();
    responders = new ArrayList<>();
    responders.add(new CreateTopicResponder());
    responders.add(new DeleteTopicResponder());
    responders.add(new ListTopicResponder());
    responders.add(new GetTopicAttributeResponder());
    responders.add(new SetTopicAttributeResponder());
    responders.add(new SubscribeResponder());
    responders.add(new ConfirmSubscriptionResponder());
    responders.add(new ListSubscriptionsResponder());
    responders.add(new ListSubscriptionsByTopicResponder());
    responders.add(new UnsubscribeResponder());
    responders.add(new SetSubscriptionAttributesResponder());
    responders.add(new GetSubscriptionAttributesResponder());
    responders.add(new PublishResponder());
  }
  
  public MockResponse createResponse(MockRequest request) {
    for(SnsResponder responder : responders) {
      if(responder.isSameAction(request)) {
        return responder.createResponse(request);
      }
    }
    return defaultResponder.createResponse(request);
  }
}
