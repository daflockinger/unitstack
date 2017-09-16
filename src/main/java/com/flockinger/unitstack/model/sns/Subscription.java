package com.flockinger.unitstack.model.sns;

import java.util.HashMap;
import java.util.Map;

public class Subscription {
  private String protocol;
  private String endpoint;
  private String subscriptionArn;
  private Map<String,String> attributes = new HashMap<>();
  
  public Map<String, String> getAttributes() {
    return attributes;
  }
  public void setAttributes(Map<String, String> attributes) {
    this.attributes = attributes;
  }
  public String getProtocol() {
    return protocol;
  }
  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }
  public String getEndpoint() {
    return endpoint;
  }
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }
  public String getSubscriptionArn() {
    return subscriptionArn;
  }
  public void setSubscriptionArn(String subscriptionArn) {
    this.subscriptionArn = subscriptionArn;
  }
}
