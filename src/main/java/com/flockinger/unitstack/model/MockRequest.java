/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
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
  private byte[] fileContent;
  
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
  public byte[] getFileContent() {
    return fileContent;
  }
  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }
  public MockRequest withFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
    return this;
  }

  public MessageUtils utils() {
    return utils;
  }
}
