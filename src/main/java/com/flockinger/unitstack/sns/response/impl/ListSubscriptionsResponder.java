package com.flockinger.unitstack.sns.response.impl;

import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class ListSubscriptionsResponder extends SnsResponder {

  private final static String LIST_SUB_ACTION = "ListSubscriptions";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return LIST_SUB_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    return new MockResponse(successBody(LIST_SUB_ACTION, getSubscriptionsXmlFrom(request.getTopics().values())));
  }
  
  private String getSubscriptionsXmlFrom(Collection<Topic> topics) {    
    String subscriptionsXml  = topics.stream()
        .map(this::getSubscriptionsXmlFrom).collect(Collectors.joining(""));
    return "<Subscriptions>" + subscriptionsXml + "</Subscriptions>";
  }
  
  protected String getSubscriptionsXmlFrom(Topic topic) {
    String subscriptionsXml = "";
    
    for(Subscription subscription : topic.getSubscriptions()) {
      subscriptionsXml += "<member><TopicArn>" + topic.getTopicArn() + "</TopicArn>"
          + "<Protocol>" + subscription.getProtocol() + "</Protocol>"
          + "<SubscriptionArn>" + subscription.getSubscriptionArn() + "</SubscriptionArn>"
          + "<Owner>" + UUID.randomUUID().toString() + "</Owner>"
          + "<Endpoint>" + subscription.getEndpoint() + "</Endpoint>"
          + "</member>";
    }
    return subscriptionsXml;
  }
}
