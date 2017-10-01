package com.flockinger.unitstack.model;

import java.util.Map;

import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.utils.MessageUtils;
import com.github.tomakehurst.wiremock.common.FileSource;

public class MockRequest {
  private Map<String,String> bodyParameters;
  private MockParameters mockParameters;
  private MessageUtils utils;
  
  private Map<String, Topic> topics;
  private Map<String, AwsQueue> queues;
  private Map<String, Bucket> buckets;
  private FileSource files;
  
  public MockRequest(Map<String, String> bodyParameters, MockParameters mockParameters,
      MessageUtils utils) {
    super();
    this.bodyParameters = bodyParameters;
    this.mockParameters = mockParameters;
    this.utils = utils;
  }

  public Map<String, String> getBodyParameters() {
    return bodyParameters;
  }
  public void setBodyParameters(Map<String, String> bodyParameters) {
    this.bodyParameters = bodyParameters;
  }
  public MockParameters getMockParameters() {
    return mockParameters;
  }
  public void setMockParameters(MockParameters mockParameters) {
    this.mockParameters = mockParameters;
  }
  public Map<String, Topic> getTopics() {
    return topics;
  }
  public void setTopics(Map<String, Topic> topics) {
    this.topics = topics;
  }
  public MockRequest withTopics(Map<String, Topic> topics) {
    this.topics = topics;
    return this;
  }
  public Map<String, AwsQueue> getQueues() {
    return queues;
  }
  public void setQueues(Map<String, AwsQueue> queues) {
    this.queues = queues;
  }
  public MockRequest withQueues(Map<String, AwsQueue> queues) {
    this.queues = queues;
    return this;
  }
  public Map<String, Bucket> getBuckets() {
    return buckets;
  }
  public void setBuckets(Map<String, Bucket> buckets) {
    this.buckets = buckets;
  }
  public MockRequest withBuckets(Map<String, Bucket> buckets) {
    this.buckets = buckets;
    return this;
  }
  public FileSource getFiles() {
    return files;
  }
  public void setFiles(FileSource files) {
    this.files = files;
  }
  public MockRequest withFiles(FileSource files) {
    this.files = files;
    return this;
  }

  public MessageUtils utils() {
    return utils;
  }
}
