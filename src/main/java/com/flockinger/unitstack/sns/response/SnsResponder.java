package com.flockinger.unitstack.sns.response;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.SnsMockParameters;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;

public abstract class SnsResponder {
  public abstract boolean isSameAction(MockRequest request);
  public abstract MockResponse createResponse(MockRequest request);
  
  private final static String ACTION_NAME = "Action";
  
  protected String successBody(String action, String content) {
    if(content == null) {
      content = "<MessageId>"+shortUid()+"</MessageId>";
    }
    
    String response = "<"+action+"Response xmlns=\"http://sns.amazonaws.com/doc/2010-03-31/\">" + 
   "<"+action+"Result> "+ content + "</"+action+"Result>"+
   "<ResponseMetadata><RequestId>"+shortUid()+"</RequestId></ResponseMetadata>"+
   "</"+action+"Response>";
    
    return response;
  }
  
  protected String errorBody(SnsMockParameters params) {
    return errorBody(params.getErrorMessage(),params.getSnsException());
  }
  
  protected String errorBody(String errorMessage, Class<?> exception) {
    return "<ErrorResponse xmlns=\"http://sns.amazonaws.com/doc/2010-03-31/\"><Error>\n" + 
        "        <Type>Sender</Type>\n" + 
        "        <Code>" + exceptionToCodeName(exception) + "</Code>\n" + 
        "        <Message>" + errorMessage + "</Message>\n" + 
        "        </Error><RequestId>" + shortUid() + "</RequestId>\n" + 
        "        </ErrorResponse>";
  }
  
  protected String getAction(Map<String,String> requestBody) {
    return requestBody.get(ACTION_NAME);
  }
  
  private String exceptionToCodeName(Class<?> exception) {
    return exception.getSimpleName().replaceAll("Exception", "");
  }
  
  private String shortUid() {
    return UUID.randomUUID().toString().substring(0,8);
  }
  
  protected String decodeValue(String value) {
    try {
      value = URLDecoder.decode(value, "utf-8");
    } catch (UnsupportedEncodingException e) {
      e.printStackTrace();
    };
    return value;
  }
  
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
