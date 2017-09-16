package com.flockinger.unitstack;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import com.amazonaws.services.sns.model.AddPermissionRequest;
import com.amazonaws.services.sns.model.AddPermissionResult;
import com.amazonaws.services.sns.model.AmazonSNSException;
import com.amazonaws.services.sns.model.CheckIfPhoneNumberIsOptedOutRequest;
import com.amazonaws.services.sns.model.CheckIfPhoneNumberIsOptedOutResult;
import com.amazonaws.services.sns.model.ConfirmSubscriptionRequest;
import com.amazonaws.services.sns.model.ConfirmSubscriptionResult;
import com.amazonaws.services.sns.model.CreatePlatformApplicationRequest;
import com.amazonaws.services.sns.model.CreatePlatformEndpointRequest;
import com.amazonaws.services.sns.model.CreateTopicRequest;
import com.amazonaws.services.sns.model.CreateTopicResult;
import com.amazonaws.services.sns.model.DeleteEndpointRequest;
import com.amazonaws.services.sns.model.DeletePlatformApplicationRequest;
import com.amazonaws.services.sns.model.DeleteTopicResult;
import com.amazonaws.services.sns.model.GetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.GetPlatformApplicationAttributesRequest;
import com.amazonaws.services.sns.model.GetSMSAttributesRequest;
import com.amazonaws.services.sns.model.GetSubscriptionAttributesRequest;
import com.amazonaws.services.sns.model.GetSubscriptionAttributesResult;
import com.amazonaws.services.sns.model.GetTopicAttributesRequest;
import com.amazonaws.services.sns.model.GetTopicAttributesResult;
import com.amazonaws.services.sns.model.InvalidParameterException;
import com.amazonaws.services.sns.model.ListEndpointsByPlatformApplicationRequest;
import com.amazonaws.services.sns.model.ListPhoneNumbersOptedOutRequest;
import com.amazonaws.services.sns.model.ListPlatformApplicationsRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicRequest;
import com.amazonaws.services.sns.model.ListSubscriptionsByTopicResult;
import com.amazonaws.services.sns.model.ListSubscriptionsResult;
import com.amazonaws.services.sns.model.ListTopicsResult;
import com.amazonaws.services.sns.model.OptInPhoneNumberRequest;
import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;
import com.amazonaws.services.sns.model.RemovePermissionRequest;
import com.amazonaws.services.sns.model.SetEndpointAttributesRequest;
import com.amazonaws.services.sns.model.SetPlatformApplicationAttributesRequest;
import com.amazonaws.services.sns.model.SetSMSAttributesRequest;
import com.amazonaws.services.sns.model.SetSubscriptionAttributesRequest;
import com.amazonaws.services.sns.model.SetSubscriptionAttributesResult;
import com.amazonaws.services.sns.model.SetTopicAttributesResult;
import com.amazonaws.services.sns.model.SubscribeRequest;
import com.amazonaws.services.sns.model.SubscribeResult;
import com.flockinger.unitstack.model.SnsMockParameters;
import com.flockinger.unitstack.model.sns.Topic;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class MockSnsTest extends UnitStackTest {

  private AmazonSNSAsync sns;

  @Before
  public void setup() {
    EndpointConfiguration endpoint =
        new EndpointConfiguration(UNIT_STACK_URL + ":" + SNS_PORT, Region.EU_Frankfurt.name());
    AWSCredentials credentials = new BasicAWSCredentials("key", "secret");
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    sns = AmazonSNSAsyncClientBuilder.standard().withEndpointConfiguration(endpoint)
        .withCredentials(credentialsProvider).build();
  }

  @Test
  public void testAddPermission_withSuccessMock_shouldWork() {
    mockSns(new SnsMockParameters());

    AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
        .withActionNames(ImmutableList.of("READ", "WRITE"))
        .withAWSAccountIds(ImmutableList.of("1", "2")).withLabel("unsecure-topic").withTopicArn(
            "arn:aws:sns:us-east-1:123456789012:my_corporate_topic:02034b43-fefa-4e07-a5eb-3be56f8c54ce");

    AddPermissionResult result = sns.addPermission(addPermissionRequest);

    assertNotNull("verify adding permission returns ok", result);
  }

  @Test(expected = AmazonSNSException.class)
  public void testAddPermission_withInvalidParameterMock_shouldThrowException() {
    mockSns(new SnsMockParameters(InvalidParameterException.class));

    AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
        .withActionNames(ImmutableList.of("READ", "WRITE"))
        .withAWSAccountIds(ImmutableList.of("1", "2")).withLabel("unsecure-topic").withTopicArn(
            "arn:aws:sns:us-east-1:123456789012:my_corporate_topic:02034b43-fefa-4e07-a5eb-3be56f8c54ce");
    
    sns.addPermission(addPermissionRequest);
  }
  
  @Test
  public void testAddPermissionAsync_shouldWork() throws InterruptedException, ExecutionException {
    mockSns(new SnsMockParameters());

    AddPermissionRequest addPermissionRequest = new AddPermissionRequest()
        .withActionNames(ImmutableList.of("READ", "WRITE"))
        .withAWSAccountIds(ImmutableList.of("1", "2")).withLabel("unsecure-topic").withTopicArn(
            "arn:aws:sns:us-east-1:123456789012:my_corporate_topic:02034b43-fefa-4e07-a5eb-3be56f8c54ce");

    Future<AddPermissionResult> result = sns.addPermissionAsync(addPermissionRequest);
    assertNotNull("verify also async call works fine", result.get());
  }
  
  
  @Test
  public void testCreateListDeleteTopic_shouldCreateReturnAndDelete() {
    mockSns(new SnsMockParameters());
    
    ListTopicsResult listTopicResultBefore = sns.listTopics();
    assertEquals("topic list should contain zero items before insert",0,listTopicResultBefore.getTopics().size());
    
    CreateTopicRequest create = new CreateTopicRequest()
        .withName("major-topic");
    CreateTopicResult createResult = sns.createTopic(create);
    String topicArn = createResult.getTopicArn();
    assertNotNull("verify returned topic ARN", topicArn);
    
    ListTopicsResult listTopicResult = sns.listTopics();
    assertEquals("after insert topic list should contain 1 item",1,listTopicResult.getTopics().size());
    assertEquals("after insert topic list should contain before inserted topic arn", topicArn, 
        listTopicResult.getTopics().get(0).getTopicArn());
    
    DeleteTopicResult deleteResult = sns.deleteTopic(topicArn);
    assertNotNull(deleteResult);
    
    ListTopicsResult listTopicsAfterDeletion = sns.listTopics();
    assertEquals("topic list should contain zero items after deletion",0,listTopicsAfterDeletion.getTopics().size());
  }
  
  @Test
  public void testGetSetTopicAttributes_shouldAddAndRespondAttributes() {
    mockSns(new SnsMockParameters());
    CreateTopicResult topicResult = sns.createTopic(new CreateTopicRequest().withName("attributefull-topic"));
    
    SetTopicAttributesResult setAttrResult = sns.setTopicAttributes(topicResult.getTopicArn(), "planet", "Omega 3");
    assertNotNull(setAttrResult);
    
    GetTopicAttributesResult topicAttributes = sns.getTopicAttributes(new GetTopicAttributesRequest().withTopicArn(topicResult.getTopicArn()));
    
    assertEquals("verify added attribute is correct", "Omega 3", topicAttributes.getAttributes().get("planet"));
    
    sns.deleteTopic(topicResult.getTopicArn());
  }
  
  
  @Test
  public void testSubscribeConfirmListUnsubscribe_shouldCreateVerifyListAndRemoveSubscription() {
    mockSns(new SnsMockParameters());
    // create topic
    CreateTopicResult topicResult = sns.createTopic(new CreateTopicRequest().withName("important-topic"));
    
    // subscribe to first topic
    SubscribeResult subscribeResult = sns.subscribe(new SubscribeRequest().withTopicArn(topicResult.getTopicArn())
        .withProtocol("sqs").withEndpoint("arn:aws:sqs:us-east-1:123456789012:queue1"));
    assertNotNull("verify subscription ARN is created", subscribeResult.getSubscriptionArn());
    
    // create second topic and subscribe to that one
    CreateTopicResult secondTopicResult = sns.createTopic(new CreateTopicRequest().withName("someOther-topic"));
    sns.subscribe(new SubscribeRequest().withTopicArn(secondTopicResult.getTopicArn())
        .withProtocol("sqs").withEndpoint("arn:aws:sqs:us-east-1:564654654:queue7"));

    // confirm first subscription
    ConfirmSubscriptionResult confirmResultAuth = sns.confirmSubscription(new ConfirmSubscriptionRequest()
        .withAuthenticateOnUnsubscribe("true").withToken("gold-token").withTopicArn(topicResult.getTopicArn()));
    assertNotNull("verify auth confirmation with responded topic arn", confirmResultAuth.getSubscriptionArn());
    ConfirmSubscriptionResult confirmResultNoAuth = sns.confirmSubscription(new ConfirmSubscriptionRequest()
        .withAuthenticateOnUnsubscribe("false").withToken("gold-token").withTopicArn(topicResult.getTopicArn()));
    assertNotNull("verify no auth confirmation with responded topic arn", confirmResultNoAuth.getSubscriptionArn());
    
    // list all subscriptions
    ListSubscriptionsResult allSubs = sns.listSubscriptions();
    assertEquals("verify correct total subscription count", 2, allSubs.getSubscriptions().size());
    com.amazonaws.services.sns.model.Subscription firstSub = allSubs.getSubscriptions().stream().filter(sub -> sub.getTopicArn().equals(topicResult.getTopicArn())).findFirst().get();
    assertEquals("verify the correct subscription arn", subscribeResult.getSubscriptionArn(), firstSub.getSubscriptionArn());
    assertEquals("verify the correct subscription topic", topicResult.getTopicArn(), firstSub.getTopicArn());
    assertEquals("verify the correct subscription protocol", "sqs", firstSub.getProtocol());
    assertEquals("verify the correct subscription endpoint", "arn:aws:sqs:us-east-1:123456789012:queue1", firstSub.getEndpoint());
    assertNotNull("verify the correct subscription owner", firstSub.getOwner());
    
    // list subscriptions of first topic
    ListSubscriptionsByTopicResult topicsSubscriptions = sns.listSubscriptionsByTopic(new ListSubscriptionsByTopicRequest(topicResult.getTopicArn()));
    assertEquals("verify that the one subscription is contained in list", 1, topicsSubscriptions.getSubscriptions().size());
    assertEquals("verify the correct subscription arn", subscribeResult.getSubscriptionArn(), topicsSubscriptions.getSubscriptions().get(0).getSubscriptionArn());
    assertEquals("verify the correct subscription topic", topicResult.getTopicArn(), topicsSubscriptions.getSubscriptions().get(0).getTopicArn());
    assertEquals("verify the correct subscription protocol", "sqs", topicsSubscriptions.getSubscriptions().get(0).getProtocol());
    assertEquals("verify the correct subscription endpoint", "arn:aws:sqs:us-east-1:123456789012:queue1", topicsSubscriptions.getSubscriptions().get(0).getEndpoint());
    assertNotNull("verify the correct subscription owner", topicsSubscriptions.getSubscriptions().get(0).getOwner());
    
    // unsubscribe first topic
    assertNotNull(sns.unsubscribe(subscribeResult.getSubscriptionArn()));
    
    // check if really unsubscribed
    ListSubscriptionsByTopicResult subsToFirstTopicAfterUnsubscribe = sns.listSubscriptionsByTopic(new ListSubscriptionsByTopicRequest(topicResult.getTopicArn()));
    assertEquals("topic should be gone", 0, subsToFirstTopicAfterUnsubscribe.getSubscriptions().size());
    
    // cleanup
    sns.deleteTopic(topicResult.getTopicArn());
    sns.deleteTopic(secondTopicResult.getTopicArn());
  }
  
  @Test
  public void testSetGetSubscriptionAttributes_shouldSetAndRespondSubscriptionAttributes() {
    mockSns(new SnsMockParameters());
    // create topic and subscription
    CreateTopicResult topicResult = sns.createTopic(new CreateTopicRequest().withName("important-topic"));
    SubscribeResult subscribeResult = sns.subscribe(new SubscribeRequest().withTopicArn(topicResult.getTopicArn())
        .withProtocol("sqs").withEndpoint("arn:aws:sqs:us-east-1:123456789012:queue1"));
    // set subscription attribute
    SetSubscriptionAttributesResult setAttrResult =  sns.setSubscriptionAttributes(new SetSubscriptionAttributesRequest()
        .withAttributeName("unicorns-exist").withAttributeValue("only in scotland").withSubscriptionArn(subscribeResult.getSubscriptionArn()));
    assertNotNull("verify setting attributes result", setAttrResult);
    // get subscription attribute
    GetSubscriptionAttributesResult subAttributes = sns.getSubscriptionAttributes(new GetSubscriptionAttributesRequest()
        .withSubscriptionArn(subscribeResult.getSubscriptionArn()));
    assertEquals("verify subscription attribute","only in scotland",subAttributes.getAttributes().get("unicorns-exist"));
  }

  @Test
  public void testPublish_shouldPublishTheMessage() {
    CreateTopicResult topicResult = sns.createTopic(new CreateTopicRequest().withName("important-topic"));
    
    PublishRequest publishReq = new PublishRequest()
        .withMessage("{\"state\":\"liquid\",\"color\":\"black\",\"waking-up\":true}")
        .withMessageStructure("json")
        .withPhoneNumber("00121212")
        .withSubject("eeffoc")
        .withTopicArn(topicResult.getTopicArn());
    
    PublishResult publishResult = sns.publish(publishReq);
    assertNotNull("verify message id",publishResult.getMessageId());
    
    Topic topic = getSnsTopics().get(topicResult.getTopicArn());
    assertEquals("verify correct message count", 1, topic.getMessages().size());
    assertEquals("verify message subject","eeffoc",topic.getMessages().get(0).getSubject());
    assertEquals("verify message body","{\"state\":\"liquid\",\"color\":\"black\",\"waking-up\":true}",topic.getMessages().get(0).getBody());
    assertEquals("verify message structure","json",topic.getMessages().get(0).getStructure());
  }
  
  @Test
  public void testNonInjectableMocks_shouldReturnNormal() {
    mockSns(new SnsMockParameters());
    
    CheckIfPhoneNumberIsOptedOutRequest phoneRequest = new CheckIfPhoneNumberIsOptedOutRequest()
        .withPhoneNumber("555123456");
    CheckIfPhoneNumberIsOptedOutResult phoneResult = sns.checkIfPhoneNumberIsOptedOut(phoneRequest);
    assertNotNull(phoneResult);
    
    CreatePlatformApplicationRequest createPlatformRequest = new CreatePlatformApplicationRequest()
        .withAttributes(ImmutableMap.of("os","oreo"))
        .withName("android").withPlatform("mobile");
    assertNotNull(sns.createPlatformApplication(createPlatformRequest));
    
    CreatePlatformEndpointRequest createPlatformEndpointReq = new CreatePlatformEndpointRequest()
        .withAttributes(ImmutableMap.of("os","lollypop"))
        .withCustomUserData("something custom")
        .withPlatformApplicationArn("mobile")
        .withToken("5-euro-token");
    assertNotNull(sns.createPlatformEndpoint(createPlatformEndpointReq));
    
    DeleteEndpointRequest deleteEndpointReq = new DeleteEndpointRequest()
        .withEndpointArn("arn:aws:sms:us-east-1:123456789012:myc:02034b43-fefa-4e07-a5e");
    assertNotNull(sns.deleteEndpoint(deleteEndpointReq));
    
    DeletePlatformApplicationRequest delPlatformAppReq = new DeletePlatformApplicationRequest()
        .withPlatformApplicationArn("arn:aws:sms:us-east-1:123456789012:myc:02034b43-fefa-4e07-a5e");
    assertNotNull(sns.deletePlatformApplication(delPlatformAppReq));
        
    GetEndpointAttributesRequest getEndpointAttr = new GetEndpointAttributesRequest();
    assertNotNull(sns.getEndpointAttributes(getEndpointAttr));
    
    assertNotNull(sns.getPlatformApplicationAttributes(
        new GetPlatformApplicationAttributesRequest().withPlatformApplicationArn("some-arn")));
    
    assertNotNull(sns.getSMSAttributes(new GetSMSAttributesRequest().withAttributes("attr1","attr2")));
    
    assertNotNull(sns.listEndpointsByPlatformApplication(new ListEndpointsByPlatformApplicationRequest()
        .withNextToken("0-euro-token").withPlatformApplicationArn("cheap-arn")));
    
    assertNotNull(sns.listPhoneNumbersOptedOut(new ListPhoneNumbersOptedOutRequest().withNextToken("plastic-token")));
    
    assertNotNull(sns.listPlatformApplications(new ListPlatformApplicationsRequest().withNextToken("wooden-token")));
    
    assertNotNull(sns.listPlatformApplications());
    
    assertNotNull(sns.optInPhoneNumber(new OptInPhoneNumberRequest().withPhoneNumber("123456789")));
    
    assertNotNull(sns.setEndpointAttributes(new SetEndpointAttributesRequest().withEndpointArn("at the end of the world")
        .withAttributes(ImmutableMap.of("some-prop","some-value"))));
    
    assertNotNull(sns.setPlatformApplicationAttributes(new SetPlatformApplicationAttributesRequest().withPlatformApplicationArn("arnn:::")
        .withAttributes(ImmutableMap.of("super","mario"))));
    
    assertNotNull(sns.setSMSAttributes(new SetSMSAttributesRequest().withAttributes(ImmutableMap.of("wtf","mfg"))));
    
    assertNotNull(sns.removePermission(new RemovePermissionRequest().withLabel("fashion label").withTopicArn("fancy topic")));
  }
}
