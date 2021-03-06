/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.model;

import java.util.Map;

import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.utils.MessageUtils;
import com.github.tomakehurst.wiremock.extension.ResponseDefinitionTransformer;

/**
 * Represents the all the data from the request, comming from <br>
 * the wiremock {@link ResponseDefinitionTransformer} implementation, <br>
 * and also all mock data and utlis ({@link MessageUtils}). <br>
 *
 */
public class MockRequest {
  private Map<String, String> bodyParameters;
  private MockParameters mockParameters;
  private MessageUtils utils;

  private Map<String, Topic> topics;
  private Map<String, AwsQueue> queues;
  private Map<String, Bucket> buckets;
  private byte[] fileContent;

  /**
   * Initialize MockRequest with body-parameters (lots of data from the request), the defined
   * mock-parameters and an instance of the MessageUtils.
   * 
   * @param bodyParameters All sorts of request data
   * @param mockParameters Mock behaviour settings
   * @param utils Basic Utilities handling response/request data
   */
  public MockRequest(Map<String, String> bodyParameters, MockParameters mockParameters,
      MessageUtils utils) {
    this.bodyParameters = bodyParameters;
    this.mockParameters = mockParameters;
    this.utils = utils;
  }

  /**
   * All The request data in map form.
   * 
   * @return All sorts of request data
   */
  public Map<String, String> getBodyParameters() {
    return bodyParameters;
  }

  /**
   * Sets the request data in map form.
   * 
   * @param bodyParameters All sorts of request data
   */
  public void setBodyParameters(Map<String, String> bodyParameters) {
    this.bodyParameters = bodyParameters;
  }

  /**
   * The predefined mock parameters.
   * 
   * @return Mock behaviour settings
   */
  public MockParameters getMockParameters() {
    return mockParameters;
  }

  /**
   * Sets the mock parameters.
   * 
   * @param mockParameters Mock behaviour settings
   */
  public void setMockParameters(MockParameters mockParameters) {
    this.mockParameters = mockParameters;
  }

  /**
   * All the mock data from SNS Topics.
   * 
   * @return All mock Topic data
   */
  public Map<String, Topic> getTopics() {
    return topics;
  }

  /**
   * Set the mock data from SNS Topics.
   * 
   * @param topics All mock Topic data
   */
  public void setTopics(Map<String, Topic> topics) {
    this.topics = topics;
  }

  /**
   * Set the mock data from SNS Topics.
   * 
   * @param topics All mock Topic data
   * @return Modified MockRequest
   */
  public MockRequest withTopics(Map<String, Topic> topics) {
    this.topics = topics;
    return this;
  }

  /**
   * All the mock data from SQS Queues.
   * 
   * @return All mock Queue data
   */
  public Map<String, AwsQueue> getQueues() {
    return queues;
  }

  /**
   * Set the mock data from SQS Queues.
   * 
   * @param queues All mock Queue data
   */
  public void setQueues(Map<String, AwsQueue> queues) {
    this.queues = queues;
  }

  /**
   * Set the mock data from SQS Queues.
   * 
   * @param queues All mock Queue data
   * @return Modified MockRequest
   */
  public MockRequest withQueues(Map<String, AwsQueue> queues) {
    this.queues = queues;
    return this;
  }

  /**
   * All the data from S3 Buckets.
   * 
   * @return All mock Bucket data
   */
  public Map<String, Bucket> getBuckets() {
    return buckets;
  }

  /**
   * Sets the data from S3 Buckets.
   * 
   * @param buckets All mock Bucket data
   */
  public void setBuckets(Map<String, Bucket> buckets) {
    this.buckets = buckets;
  }

  /**
   * Sets the data from S3 Buckets.
   * 
   * @param buckets All mock Bucket data
   * @return Modified MockRequest
   */
  public MockRequest withBuckets(Map<String, Bucket> buckets) {
    this.buckets = buckets;
    return this;
  }

  /**
   * The data to upload in form of an byte array.
   * 
   * @return Uploaded binary data
   */
  public byte[] getFileContent() {
    return fileContent;
  }

  /**
   * Sets the data to upload.
   * 
   * @param fileContent Uploaded binary data
   */
  public void setFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
  }

  /**
   * Sets the data to upload.
   * 
   * @param fileContent Uploaded binary data
   * @return Modified MockRequest
   */
  public MockRequest withFileContent(byte[] fileContent) {
    this.fileContent = fileContent;
    return this;
  }

  /**
   * Get utilities, helps handling/transforming request/response data.
   * 
   * @return Basic Utilities handling response/request data
   */
  public MessageUtils utils() {
    return utils;
  }
}
