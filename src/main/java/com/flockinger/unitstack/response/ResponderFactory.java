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
import com.flockinger.unitstack.response.sqs.ChangeVisibilityResponder;
import com.flockinger.unitstack.response.sqs.CreateQueueResponder;
import com.flockinger.unitstack.response.sqs.DeleteMessageBatchResponder;
import com.flockinger.unitstack.response.sqs.DeleteMessageResponder;
import com.flockinger.unitstack.response.sqs.DeleteQueueResponder;
import com.flockinger.unitstack.response.sqs.GetQueueUrlResponder;
import com.flockinger.unitstack.response.sqs.ListQueuesResponder;
import com.flockinger.unitstack.response.sqs.PurgeQueueResponder;
import com.flockinger.unitstack.response.sqs.ReceiveMessageResponder;
import com.flockinger.unitstack.response.sqs.SendMessageBatchResponder;
import com.flockinger.unitstack.response.sqs.SendMessageResponder;

public class ResponderFactory implements Responder {
  private List<Responder> responders;
  private Responder defaultResponder;
  
  public ResponderFactory() {
    responders = new ArrayList<>();
    defaultResponder = new DefaultResponder();
  }
 
  
  public MockResponse createResponse(MockRequest request) {
    for(Responder responder : responders) {
      if(responder.isSameAction(request) && shouldBeSuccessfull(request)) {
        return responder.createResponse(request);
      }
    }
    return defaultResponder.createResponse(request);
  }
  
  private boolean shouldBeSuccessfull(MockRequest request) {
    return request.getMockParameters() == null 
        || request.getMockParameters().isRequestSuccessfull();
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
    sqsResponderFactory.add(new SendMessageResponder());
    sqsResponderFactory.add(new ReceiveMessageResponder());
    sqsResponderFactory.add(new ChangeVisibilityResponder());
    sqsResponderFactory.add(new DeleteMessageResponder());
    sqsResponderFactory.add(new DeleteQueueResponder());
    sqsResponderFactory.add(new PurgeQueueResponder());
    sqsResponderFactory.add(new SendMessageBatchResponder());
    sqsResponderFactory.add(new DeleteMessageBatchResponder());
    
    return sqsResponderFactory;
  }
}
