package com.flockinger.unitstack.sns.response.impl;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.sns.response.SnsResponder;

public class ListSubscriptionsByTopicResponder extends ListSubscriptionsResponder {

  private final static String LIST_SUB_BY_TOPIC_ACTION = "ListSubscriptionsByTopic";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return LIST_SUB_BY_TOPIC_ACTION.equals(getAction(request.getBodyParameters()));
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String content = "";
    
    if(request.getTopics().containsKey(topicArn)) {
      content = getSubscriptionsXmlFrom(request.getTopics().get(topicArn));
    }
    return new MockResponse(successBody(LIST_SUB_BY_TOPIC_ACTION, 
        "<Subscriptions>" + content + "</Subscriptions>"));
  }
}
