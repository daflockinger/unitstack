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
package com.flockinger.unitstack;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.any;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;

import com.amazonaws.services.sns.model.AmazonSNSException;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.model.sqs.AwsQueue;
import com.flockinger.unitstack.transformer.S3RequestTransformer;
import com.flockinger.unitstack.transformer.SnsRequestTransformer;
import com.flockinger.unitstack.transformer.SqsRequestTransformer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

/**
 * <p>
 * Abstract base class for mocking implementations using AWS (Amazon Web Services).
 * </p>
 * <p>
 * Provides basic stub-services for mocking/stubbing communication from/to 
 * the AWS during unit tests.
 * </p>
 */
public abstract class UnitStackTest {
  
  /**
   * Base URL for the AWS stub.
   */
  public final static String UNIT_STACK_URL = "http://localhost";
  
  /**
   * Port for SNS web services.
   */
  public final static int SNS_PORT = 4575;
  
  /**
   * Port for SQS web services.
   */
  public final static int SQS_PORT = 4576;
  
  /**
   * Port for S3 web services.
   */
  public final static int S3_PORT = 4572;
  
  public final static String MOCK_PARAMS="MOCK_PARAMS";
  
  private Map<String, Topic> snsTopics = new HashMap<>();
  private Map<String, AwsQueue> queues = new HashMap<>();
  private Map<String, Bucket> buckets = new HashMap<>();
  
  @Rule
  public WireMockRule snsMockRule = new WireMockRule(WireMockConfiguration.options()
      .port(SNS_PORT)
      .extensions(new SnsRequestTransformer(snsTopics)));
  
  @Rule
  public WireMockRule sqsMockRule = new WireMockRule(WireMockConfiguration.options()
      .port(SQS_PORT)
      .extensions(new SqsRequestTransformer(queues)));
  
  @Rule
  public WireMockRule s3MockRule = new WireMockRule(WireMockConfiguration.options()
      .port(S3_PORT)
      .extensions(new S3RequestTransformer(buckets)));
  
  /**
   * <p>
   * Initializes mock for SNS (Simple Notification Service)
   * </p>
   * 
   * @param mockParameters
   */
  protected <T extends AmazonSNSException> void mockSns(MockParameters mockParameters) {
    snsMockRule.stubFor(post("/").willReturn(aResponse()
        .withTransformerParameter(MOCK_PARAMS, mockParameters)));
  }
  
  /**
   * <p>
   * Initializes mock for SQS (Simple Queue Service)
   * </p>
   * 
   * @param mockParameters
   */
  protected <T extends AmazonSNSException> void mockSqs(MockParameters mockParameters) {
    sqsMockRule.stubFor(post(urlPathMatching("/.*")).willReturn(aResponse()
        .withTransformerParameter(MOCK_PARAMS, mockParameters)));
  }
  
  
  /**
   * <p>
   * Initializes mock for S3 (Simple Storage Service)
   * </p>
   * 
   * @param mockParameters
   */
  protected <T extends AmazonSNSException> void mockS3(MockParameters mockParameters) {
    s3MockRule.stubFor(any(urlPathMatching("/.*")).willReturn(aResponse()
        .withTransformerParameter(MOCK_PARAMS, mockParameters)));
  }

  /**
   * Returns mock data for SNS Topics.
   * 
   * @return
   */
  protected Map<String, Topic> getSnsTopics() {
    return snsTopics;
  }

  /**
   * Returns mock data for SQS Queues.
   * 
   * @return
   */
  public Map<String, AwsQueue> getQueues() {
    return queues;
  }

  /**
   * Returns mock data for S3 Buckets.
   * 
   * @return
   */
  public Map<String, Bucket> getBuckets() {
    return buckets;
  }
}
