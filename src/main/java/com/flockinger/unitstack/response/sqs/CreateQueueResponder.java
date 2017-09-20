package com.flockinger.unitstack.response.sqs;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.AwsQueue;


public class CreateQueueResponder extends SqsResponder {

  private final static String CREATE_QUEUE_ACTION = "CreateQueue";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(CREATE_QUEUE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String queueName = request.getBodyParameters().get("QueueName");
    String url = "";
    
    if(StringUtils.isNotEmpty(queueName)) {
      url = UnitStackTest.UNIT_STACK_URL + ":"  + UnitStackTest.SQS_PORT + "/123456789012/" + queueName;
      AwsQueue queue = new AwsQueue(queueName, url);
      request.getQueues().put(queueName,queue);
    }
    return new MockResponse(request.utils().successBody(CREATE_QUEUE_ACTION, "<QueueUrl>" + url + "</QueueUrl>"));
  }

}
