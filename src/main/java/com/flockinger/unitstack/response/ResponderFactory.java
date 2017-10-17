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
package com.flockinger.unitstack.response;

import java.util.ArrayList;
import java.util.List;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.s3.CopyObjectResponder;
import com.flockinger.unitstack.response.s3.CreateBucketResponder;
import com.flockinger.unitstack.response.s3.DefaultS3Responder;
import com.flockinger.unitstack.response.s3.DeleteBucketResponder;
import com.flockinger.unitstack.response.s3.DeleteObjectResponder;
import com.flockinger.unitstack.response.s3.DeleteObjectsResponder;
import com.flockinger.unitstack.response.s3.DoesObjectExistResponder;
import com.flockinger.unitstack.response.s3.GetBucketAclResponder;
import com.flockinger.unitstack.response.s3.GetBucketLocationResponder;
import com.flockinger.unitstack.response.s3.GetObjectAclResponder;
import com.flockinger.unitstack.response.s3.GetObjectResponder;
import com.flockinger.unitstack.response.s3.ListBucketsResponder;
import com.flockinger.unitstack.response.s3.ListObjectsResponder;
import com.flockinger.unitstack.response.s3.MultipartUploadResponder;
import com.flockinger.unitstack.response.s3.PutObjectResponder;
import com.flockinger.unitstack.response.s3.SetBucketAclResponder;
import com.flockinger.unitstack.response.s3.SetObjectAclResponder;
import com.flockinger.unitstack.response.sns.ConfirmSubscriptionResponder;
import com.flockinger.unitstack.response.sns.CreateTopicResponder;
import com.flockinger.unitstack.response.sns.DeleteTopicResponder;
import com.flockinger.unitstack.response.sns.GetSubscriptionAttributesResponder;
import com.flockinger.unitstack.response.sns.GetTopicAttributeResponder;
import com.flockinger.unitstack.response.sns.ListSubscriptionsByTopicResponder;
import com.flockinger.unitstack.response.sns.ListSubscriptionsResponder;
import com.flockinger.unitstack.response.sns.ListTopicResponder;
import com.flockinger.unitstack.response.sns.PublishResponder;
import com.flockinger.unitstack.response.sns.SetSubscriptionAttributesResponder;
import com.flockinger.unitstack.response.sns.SetTopicAttributeResponder;
import com.flockinger.unitstack.response.sns.SubscribeResponder;
import com.flockinger.unitstack.response.sns.UnsubscribeResponder;
import com.flockinger.unitstack.response.sqs.ChangeVisibilityResponder;
import com.flockinger.unitstack.response.sqs.CreateQueueResponder;
import com.flockinger.unitstack.response.sqs.DeleteMessageBatchResponder;
import com.flockinger.unitstack.response.sqs.DeleteMessageResponder;
import com.flockinger.unitstack.response.sqs.DeleteQueueResponder;
import com.flockinger.unitstack.response.sqs.GetQueueUrlResponder;
import com.flockinger.unitstack.response.sqs.ListQueuesResponder;
import com.flockinger.unitstack.response.sqs.PurgeQueueResponder;
import com.flockinger.unitstack.response.sqs.ReceiveMessageResponder;
import com.flockinger.unitstack.response.sqs.SendMessageBatchResponder;
import com.flockinger.unitstack.response.sqs.SendMessageResponder;

/**
 * Factory creating smart responders for AWS services.
 *
 */
public class ResponderFactory implements Responder {
  private List<Responder> responders;
  private Responder defaultResponder;
  
  public ResponderFactory() {
    responders = new ArrayList<>();
    defaultResponder = new DefaultResponder();
  }
 
  
  public MockResponse createResponse(MockRequest request) {
    for(Responder responder : responders) {
      if(responder.isSameAction(request) && shouldBeSuccessfull(request)) {
        return responder.createResponse(request);
      }
    }
    return defaultResponder.createResponse(request);
  }
  
  private boolean shouldBeSuccessfull(MockRequest request) {
    return request.getMockParameters() == null 
        || request.getMockParameters().isRequestSuccessfull();
  }
  
  public boolean add(Responder responder) {
    return responders.add(responder);
  }

  public void setDefaultResponder(Responder defaultResponder) {
    this.defaultResponder = defaultResponder;
  }
  
  /**
   * Responder for all SNS requests.
   * 
   * @return
   */
  public static Responder snsResponder() {
    ResponderFactory snsResponderFactory = new ResponderFactory();
    snsResponderFactory.add(new CreateTopicResponder());
    snsResponderFactory.add(new DeleteTopicResponder());
    snsResponderFactory.add(new ListTopicResponder());
    snsResponderFactory.add(new GetTopicAttributeResponder());
    snsResponderFactory.add(new SetTopicAttributeResponder());
    snsResponderFactory.add(new SubscribeResponder());
    snsResponderFactory.add(new ConfirmSubscriptionResponder());
    snsResponderFactory.add(new ListSubscriptionsResponder());
    snsResponderFactory.add(new ListSubscriptionsByTopicResponder());
    snsResponderFactory.add(new UnsubscribeResponder());
    snsResponderFactory.add(new SetSubscriptionAttributesResponder());
    snsResponderFactory.add(new GetSubscriptionAttributesResponder());
    snsResponderFactory.add(new PublishResponder());
    
    return snsResponderFactory;
  }
  
  /**
 * Responder for all SQS requests.
   * 
   * @return
   */
  public static Responder sqsResponder() {
    ResponderFactory sqsResponderFactory = new ResponderFactory();
    sqsResponderFactory.add(new CreateQueueResponder());
    sqsResponderFactory.add(new GetQueueUrlResponder());
    sqsResponderFactory.add(new ListQueuesResponder());
    sqsResponderFactory.add(new SendMessageResponder());
    sqsResponderFactory.add(new ReceiveMessageResponder());
    sqsResponderFactory.add(new ChangeVisibilityResponder());
    sqsResponderFactory.add(new DeleteMessageResponder());
    sqsResponderFactory.add(new DeleteQueueResponder());
    sqsResponderFactory.add(new PurgeQueueResponder());
    sqsResponderFactory.add(new SendMessageBatchResponder());
    sqsResponderFactory.add(new DeleteMessageBatchResponder());
    
    return sqsResponderFactory;
  }
  
  /**
   * Responder for all S3 requests.
   * 
   * @return
   */
  public static Responder s3Responder() {
    ResponderFactory s3ResponderFactory = new ResponderFactory();
    s3ResponderFactory.add(new CreateBucketResponder());
    s3ResponderFactory.add(new ListBucketsResponder());
    s3ResponderFactory.add(new GetBucketLocationResponder());
    s3ResponderFactory.add(new DeleteBucketResponder());
    s3ResponderFactory.add(new SetBucketAclResponder());
    s3ResponderFactory.add(new GetBucketAclResponder());
    s3ResponderFactory.add(new PutObjectResponder());
    s3ResponderFactory.add(new GetObjectResponder());
    s3ResponderFactory.add(new CopyObjectResponder());
    s3ResponderFactory.add(new DoesObjectExistResponder());
    s3ResponderFactory.add(new DeleteObjectResponder());
    s3ResponderFactory.add(new DeleteObjectResponder());
    s3ResponderFactory.add(new SetObjectAclResponder());
    s3ResponderFactory.add(new GetObjectAclResponder());
    s3ResponderFactory.add(new ListObjectsResponder());
    s3ResponderFactory.add(new DeleteObjectsResponder());
    s3ResponderFactory.add(new MultipartUploadResponder());
    s3ResponderFactory.setDefaultResponder(new DefaultS3Responder());
    
    return s3ResponderFactory;
  }
}
