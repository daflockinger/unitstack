package com.flockinger.unitstack.response.sns;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.response.Responder;

abstract class SnsResponder implements Responder {
  
  public abstract boolean isSameAction(MockRequest request);
  public abstract MockResponse createResponse(MockRequest request);

  
  protected String getAttributeResponseXml(Map<String,String> attributes) {
    String attributesXml = "";
    
    for(String attributeName : attributes.keySet()) {
      attributesXml += "<entry>" + "<key>" + attributeName + "</key>"  + 
    "<value>" + attributes.get(attributeName) + "</value>" + "</entry>";
    }
    return "<Attributes>" + attributesXml + "</Attributes>";
  }
  
  protected Optional<Subscription> findSubscriptionWithArn(String subscriptionArn, Collection<Topic> topics) {
    Optional<Topic> topic = findTopicWithSubscription(subscriptionArn,topics);
    Optional<Subscription> subscription = Optional.empty();
    if(topic.isPresent()) {
      subscription = topic.get().getSubscriptions().stream()
          .filter(sub -> hasSubscriptionArn(subscriptionArn,sub)).findFirst();
    }
    return subscription;
  }
  
  protected Optional<Topic> findTopicWithSubscription(String subscriptionArn, Collection<Topic> topics) {
    return topics.stream().filter(topic -> 
      topic.getSubscriptions().stream().anyMatch(subscription -> 
         hasSubscriptionArn(subscriptionArn,subscription))
    ).findAny();
  }
  
  protected boolean hasSubscriptionArn(String arn, Subscription subscription) {
    return StringUtils.equals(arn, subscription.getSubscriptionArn());
  }
}
