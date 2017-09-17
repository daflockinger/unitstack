package com.flockinger.unitstack;

import static com.amazonaws.util.StringUtils.UTF8;
import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.MessageMD5ChecksumHandler;
import com.amazonaws.services.sqs.model.AddPermissionRequest;
import com.amazonaws.services.sqs.model.AddPermissionResult;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityRequest;
import com.amazonaws.services.sqs.model.ChangeMessageVisibilityResult;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.CreateQueueResult;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteMessageResult;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.DeleteQueueResult;
import com.amazonaws.services.sqs.model.GetQueueAttributesRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlRequest;
import com.amazonaws.services.sqs.model.GetQueueUrlResult;
import com.amazonaws.services.sqs.model.ListDeadLetterSourceQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.ReceiveMessageResult;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageResult;
import com.amazonaws.services.sqs.model.SetQueueAttributesRequest;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.Md5Utils;
import com.flockinger.unitstack.model.MockParameters;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MockSqsTest extends UnitStackTest {

  private AmazonSQS sqs;
 
  @Before
  public void setup() {
    mockSqs(new MockParameters());
    
    EndpointConfiguration endpoint =
        new EndpointConfiguration(UNIT_STACK_URL + ":" + SQS_PORT, Region.EU_Frankfurt.name());
    AWSCredentials credentials = new BasicAWSCredentials("key", "secret");
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    sqs = AmazonSQSAsyncClientBuilder.standard().withEndpointConfiguration(endpoint)
        .withCredentials(credentialsProvider).build();
  }
  
  @Test
  public void testCreateGetUrlListQueue_shouldCreateReturnUrlAndListQueue() {
    // create first queue
    CreateQueueResult createdQueue = sqs.createQueue(new CreateQueueRequest().withQueueName("tea-earl-grey-queue"));
    assertNotNull("verify that, on creation, queue url was returned",createdQueue.getQueueUrl());
    // create other queues
    CreateQueueResult secondTeaQueue = sqs.createQueue(new CreateQueueRequest().withQueueName("tea-mate-queue"));
    CreateQueueResult anotherQueue = sqs.createQueue(new CreateQueueRequest().withQueueName("coffee-queue"));
    // get queue url
    GetQueueUrlResult queueUrlResult = sqs.getQueueUrl(new GetQueueUrlRequest()
        .withQueueName("tea-earl-grey-queue").withQueueOwnerAWSAccountId("some owner"));
    assertNotNull("verify that, on fetch, queue url was returned", queueUrlResult.getQueueUrl());
    // get all queues
    ListQueuesResult allQueues = sqs.listQueues();
    assertEquals("verify all queues are returned", 3, allQueues.getQueueUrls().size());
    assertTrue("verify that all queues contain first queue", allQueues.getQueueUrls().contains(createdQueue.getQueueUrl()));
    assertTrue("verify that all queues contain second tea queue", allQueues.getQueueUrls().contains(secondTeaQueue.getQueueUrl()));
    assertTrue("verify that all queues contain coffee queue", allQueues.getQueueUrls().contains(anotherQueue.getQueueUrl()));
    // get only queues that start with 'tea'
    ListQueuesResult teaQueues = sqs.listQueues(new ListQueuesRequest("tea"));
    assertEquals("verify only tea queues are returned", 2, teaQueues.getQueueUrls().size());
    assertTrue("verify that tea queues contain first queue", teaQueues.getQueueUrls().contains(createdQueue.getQueueUrl()));
    assertTrue("verify that tea queues contain second tea queue", teaQueues.getQueueUrls().contains(secondTeaQueue.getQueueUrl()));
  
    // cleanup
    getQueues().remove("tea-earl-grey-queue");
    getQueues().remove("tea-mate-queue");
    getQueues().remove("coffee-queue");
  }
  
  @Test
  public void testCreatQueue_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.createQueue(new CreateQueueRequest()));
  }
  
  @Test
  public void testListQueues_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.listQueues(new ListQueuesRequest()));
  }
  
  @Test
  public void testGetQueueUrl_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.getQueueUrl(new GetQueueUrlRequest()));
  }
  
  @Test
  public void testDeleteQueue_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.deleteQueue(new DeleteQueueRequest()));
  }
 
  
 // @Test
  public void testSendChangeVisibilityReceiveDeleteMessage_shouldSendChangeVisibilityReceiveAndDeleteMessage() {
    // create queue
    CreateQueueResult createdQueue = sqs.createQueue(new CreateQueueRequest().withQueueName("tea-earl-grey-queue"));
    // send message
    String messageBody = "{\"life-universe-everything\":42}";
    SendMessageResult sendResult = sqs.sendMessage(new SendMessageRequest().withDelaySeconds(0).withMessageBody(messageBody)
        .withMessageGroupId("some-group-id-123").withQueueUrl(createdQueue.getQueueUrl()));
    assertNotNull("message sending returned ok", sendResult);
    assertNotNull("verify body MD5 exists",sendResult.getMD5OfMessageBody());
    assertNotNull("verify message id exists",sendResult.getMessageId());
    assertNotNull("verify sequence number exists",sendResult.getSequenceNumber());
   
    // receive message
    ReceiveMessageResult messageResult = sqs.receiveMessage(new ReceiveMessageRequest()
        .withMaxNumberOfMessages(1).withQueueUrl(createdQueue.getQueueUrl()).withVisibilityTimeout(10)
        .withWaitTimeSeconds(0));
    assertNotNull("verify received message returned ok",messageResult);
    assertEquals("verify correct receive count", 1, messageResult.getMessages().size());
    Message firstMessage = messageResult.getMessages().get(0);
    assertEquals("verify correct body returned",messageBody,firstMessage.getBody());
    assertEquals("verify correct message MD5",getAwsMessageMD5(messageBody),firstMessage.getMD5OfBody());
    assertNotNull("verify message id exists",firstMessage.getMessageId());
    assertNotNull("verify receipt handle exists",firstMessage.getReceiptHandle());
    
    // extend visibility timeout
    ChangeMessageVisibilityResult visibilityResult = sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest()
        .withQueueUrl(createdQueue.getQueueUrl()).withReceiptHandle(firstMessage.getReceiptHandle()).withVisibilityTimeout(40));
    assertNotNull("changing visibility returned ok", visibilityResult);
    
    // verify if message is invisible
    ReceiveMessageResult emptyResult = sqs.receiveMessage(new ReceiveMessageRequest()
        .withMaxNumberOfMessages(1).withQueueUrl(createdQueue.getQueueUrl()).withVisibilityTimeout(20)
        .withWaitTimeSeconds(0));
    assertTrue("at visibility timeout the message should not be available.", emptyResult.getMessages().isEmpty());
    
    // delete message from queue
    DeleteMessageResult deleteResult = sqs.deleteMessage(new DeleteMessageRequest()
        .withQueueUrl(createdQueue.getQueueUrl()).withReceiptHandle(firstMessage.getReceiptHandle()));
    assertNotNull("verify deletion returned ok",deleteResult);
  };
  
  private String getAwsMessageMD5(String message) {
    byte[] expectedMd5 = null;
    try {
      expectedMd5 = Md5Utils.computeMD5Hash(message.getBytes(UTF8));
    } catch (Exception e) {}
    return BinaryUtils.toHex(expectedMd5);
  }
  
  @Test
  public void testReceiveMessage_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.receiveMessage(new ReceiveMessageRequest()));
  }
  
  @Test
  public void testChangeMessageVisibility_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.changeMessageVisibility(new ChangeMessageVisibilityRequest()));
  }
  
  @Test
  public void testDeleteMessage_withEmptyRequestParams_shouldWork() {
    assertNotNull(sqs.deleteMessage(new DeleteMessageRequest()));
  }
  
  /*
   * Test SOLO:
   
   receiveMessage
   changeMessageVisibility
   deleteMessage
   sendMessage
   
   deleteMessageBatch
   changeMessageVisibilityBatch
   sendMessageBatch
   purgeQueue
   
     @Test
  public void test_with_should() {
    
  }
   */  
  
  @Test
  public void testNonInjectableMocks_shouldReturnNormal() { 
    assertNotNull(sqs.deleteQueue(new DeleteQueueRequest().withQueueUrl("zero")));
    assertNotNull(sqs.addPermission(new AddPermissionRequest().withActions("one").withAWSAccountIds("two","three").withLabel("four").withQueueUrl("five")));
    assertNotNull(sqs.listDeadLetterSourceQueues(new ListDeadLetterSourceQueuesRequest().withQueueUrl("ten")));
    assertNotNull(sqs.getQueueAttributes(new GetQueueAttributesRequest().withAttributeNames(ImmutableList.of("eleven")).withQueueUrl("twelve")));
    assertNotNull(sqs.setQueueAttributes(new SetQueueAttributesRequest().withAttributes(ImmutableMap.of("thirteen","fourteen")).withQueueUrl("fifteen")));
  }
}
