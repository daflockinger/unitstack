package com.flockinger.unitstack.response;

import java.util.ArrayList;
import java.util.List;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.sns.ConfirmSubscriptionResponder;
import com.flockinger.unitstack.response.sns.CreateTopicResponder;
import com.flockinger.unitstack.response.sns.DeleteTopicResponder;
import com.flockinger.unitstack.response.sns.GetSubscriptionAttributesResponder;
import com.flockinger.unitstack.response.sns.GetTopicAttributeResponder;
import com.flockinger.unitstack.response.sns.ListSubscriptionsByTopicResponder;
import com.flockinger.unitstack.response.sns.ListSubscriptionsResponder;
import com.flockinger.unitstack.response.sns.ListTopicResponder;
import com.flockinger.unitstack.response.sns.PublishResponder;
import com.flockinger.unitstack.response.sns.SetSubscriptionAttributesResponder;
import com.flockinger.unitstack.response.sns.SetTopicAttributeResponder;
import com.flockinger.unitstack.response.sns.SubscribeResponder;
import com.flockinger.unitstack.response.sns.UnsubscribeResponder;
import com.flockinger.unitstack.response.sqs.CreateQueueResponder;
import com.flockinger.unitstack.response.sqs.GetQueueUrlResponder;
import com.flockinger.unitstack.response.sqs.ListQueuesResponder;

public class ResponderFactory implements Responder {
  private List<Responder> responders;
  private Responder defaultResponder;
  
  public ResponderFactory() {
    responders = new ArrayList<>();
    defaultResponder = new DefaultResponder();
  }
 
  
  public MockResponse createResponse(MockRequest request) {
    for(Responder responder : responders) {
      if(responder.isSameAction(request)) {
        return responder.createResponse(request);
      }
    }
    return defaultResponder.createResponse(request);
  }
  
  public boolean add(Responder responder) {
    return responders.add(responder);
  }

  public void setDefaultResponder(Responder defaultResponder) {
    this.defaultResponder = defaultResponder;
  }
  
  public static Responder snsResponder() {
    ResponderFactory snsResponderFactory = new ResponderFactory();
    snsResponderFactory.add(new CreateTopicResponder());
    snsResponderFactory.add(new DeleteTopicResponder());
    snsResponderFactory.add(new ListTopicResponder());
    snsResponderFactory.add(new GetTopicAttributeResponder());
    snsResponderFactory.add(new SetTopicAttributeResponder());
    snsResponderFactory.add(new SubscribeResponder());
    snsResponderFactory.add(new ConfirmSubscriptionResponder());
    snsResponderFactory.add(new ListSubscriptionsResponder());
    snsResponderFactory.add(new ListSubscriptionsByTopicResponder());
    snsResponderFactory.add(new UnsubscribeResponder());
    snsResponderFactory.add(new SetSubscriptionAttributesResponder());
    snsResponderFactory.add(new GetSubscriptionAttributesResponder());
    snsResponderFactory.add(new PublishResponder());
    
    return snsResponderFactory;
  }
  
  public static Responder sqsResponder() {
    ResponderFactory sqsResponderFactory = new ResponderFactory();
    sqsResponderFactory.add(new CreateQueueResponder());
    sqsResponderFactory.add(new GetQueueUrlResponder());
    sqsResponderFactory.add(new ListQueuesResponder());
    return sqsResponderFactory;
  }
}
