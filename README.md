# UnitStack

Unitstack provides a very fast and easy way to test your (AWS) cloud application in a simple unit-test.
It consists of mocks/stubs of some of the services provided by the Amazon Web Services:
* **SNS** (Simple Notification Service)
* **SQS** (Simple Queue Service)
* **S3** (Simple Storage Service)
* ... more services will be added by time!

Unitstack's stub services behave like the real ones. You get real responses, 
e.g. when you send messages to your SQS, and fetch messages later, you'll receive exactly 
the messages you sent. 
You can also verify that the correct data is sent by accessing the service data 
stored by UnitStack (e.g. Topics, Buckets with Objects and their file contents,...).

## Getting Started

### Example Implementation

To use UnitStack in your unit tests:
* extend your test class with the ``UnitStackTest`` class. 
* initiate the service mock by calling the ``mockS3`` method with either
empty or specific mock parameters
* point the AWS client to the mock endpoint by using the
``UNIT_STACK_URL`` and the correct service port, in this case ``SNS_PORT``.

After that, your're ready to go with unit-testing your AWS implementation locally.
Here's the example:
```
public class ExampleSnsTest extends UnitStackTest {

  private AmazonSNSAsync sns;

  @Before
  public void setup() {
    mockS3(new MockParameters());
    EndpointConfiguration endpoint =
        new EndpointConfiguration(UNIT_STACK_URL + ":" + SNS_PORT, Region.EU_Frankfurt.name());
    AWSCredentials credentials = new BasicAWSCredentials("key", "secret");
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    sns = AmazonSNSAsyncClientBuilder.standard().withEndpointConfiguration(endpoint)
        .withCredentials(credentialsProvider).build();
  } 

  @Test
  public void testPublish_shouldPublishTheMessage() {
    CreateTopicResult topicResult = sns.createTopic(new CreateTopicRequest().withName("important-topic"));
    
    PublishRequest publishReq = new PublishRequest()
        .withMessage("{\"state\":\"liquid\",\"color\":\"black\",\"waking-up\":true}")
        .withMessageStructure("json")
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
}
```
Other good examples can be found in the test classes from UnitStack, since it's tested against the AWS SDK.

## Built With

* [WireMock](https://github.com/tomakehurst/wiremock) - Responsible for creating/handling the stub webservices
* [jackson-databind](https://github.com/FasterXML/jackson-databind) - Handling all the JSON and XML stuff
* [AWS-SDK](https://github.com/aws/aws-sdk-java) - UnitStack is tested against the latest version of the AWS SDK


## Versioning

We use [SemVer](http://semver.org/) for versioning. 

## Authors

* **Florian Mitterbauer** - *Initial work* - [daflockinger](https://github.com/daflockinger)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/daflockinger/unitstack/blob/master/LICENSE) file for details

## Acknowledgments
The project was very much inspired by [LocalStack](https://github.com/localstack/localstack) and my need for a simple and fast 
tool to unit test AWS implementations without the hassle of connecting it to the real AWS services for testing.
