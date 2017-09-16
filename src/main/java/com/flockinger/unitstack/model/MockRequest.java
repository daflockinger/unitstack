package com.flockinger.unitstack.model;

import java.util.Map;

import com.flockinger.unitstack.model.sns.Topic;

public class MockRequest {
  private Map<String,String> bodyParameters;
  private SnsMockParameters mockParameters;
  private Map<String, Topic> topics;
  
  public MockRequest(Map<String, String> bodyParameters, SnsMockParameters mockParameters,
      Map<String, Topic> topics) {
    super();
    this.bodyParameters = bodyParameters;
    this.mockParameters = mockParameters;
    this.topics = topics;
  }
  
  public Map<String, String> getBodyParameters() {
    return bodyParameters;
  }
  public void setBodyParameters(Map<String, String> bodyParameters) {
    this.bodyParameters = bodyParameters;
  }
  public SnsMockParameters getMockParameters() {
    return mockParameters;
  }
  public void setMockParameters(SnsMockParameters mockParameters) {
    this.mockParameters = mockParameters;
  }
  public Map<String, Topic> getTopics() {
    return topics;
  }
  public void setTopics(Map<String, Topic> topics) {
    this.topics = topics;
  }
}
