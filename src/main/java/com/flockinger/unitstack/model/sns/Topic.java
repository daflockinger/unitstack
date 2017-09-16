package com.flockinger.unitstack.model.sns;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Topic {
  private String name;
  private String topicArn;
  private List<SnsMessage> messages = new ArrayList<>();
  List<Subscription> subscriptions = new ArrayList<>();
  private Map<String,String> attributes = new HashMap<>();
  
  public Topic() {}
  
  public Topic(String name, String topicArn) {
    super();
    this.name = name;
    this.topicArn = topicArn;
  }
  
  public Map<String, String> getAttributes() {
    return attributes;
  }
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getTopicArn() {
    return topicArn;
  }
  public void setTopicArn(String topicArn) {
    this.topicArn = topicArn;
  }
  public List<SnsMessage> getMessages() {
    return messages;
  }
  public void setMessages(List<SnsMessage> messages) {
    this.messages = messages;
  }
  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }
  public void setSubscriptions(List<Subscription> subscriptions) {
    this.subscriptions = subscriptions;
  }
}
