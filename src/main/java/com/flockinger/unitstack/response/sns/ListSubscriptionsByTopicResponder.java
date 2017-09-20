package com.flockinger.unitstack.response.sns;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;

public class ListSubscriptionsByTopicResponder extends ListSubscriptionsResponder {

  private final static String LIST_SUB_BY_TOPIC_ACTION = "ListSubscriptionsByTopic";
  
  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(LIST_SUB_BY_TOPIC_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String content = "";
    
    if(request.getTopics().containsKey(topicArn)) {
      content = getSubscriptionsXmlFrom(request.getTopics().get(topicArn));
    }
    return new MockResponse(request.utils().successBody(LIST_SUB_BY_TOPIC_ACTION, 
        "<Subscriptions>" + content + "</Subscriptions>"));
  }
}
