# UnitStack
[![Build Status](https://travis-ci.org/daflockinger/unitstack.svg?branch=master)](https://travis-ci.org/daflockinger/unitstack)
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

### Setup
Just add the dependency to your pom.xml:
```
<dependency>
    <groupId>com.flockinger</groupId>
    <artifactId>unitstack</artifactId>
    <version>0.1.1</version>
</dependency>
```

### Example Implementation

To use UnitStack in your unit tests:
* extend your test class with the ``UnitStackTest`` class. 
* initiate the service mock by calling the ``mockSns`` method with either
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

##### S3 implementation requirements

If your system has IPv4 routing, you need to create hosts for the mocked S3 buckets in your ``/etc/hosts`` file. 
For example if your tests contain a bucket with the name 'specialbucket', the following line must be added
at the end of your ``/etc/hosts`` file:
```
127.0.0.1       specialbucket.localhost
```

### Development on UnitStack

The UnitStack tests should run fine out of the box if your system has an IPv6 routing, 
otherwise you will probably need to run the ``createS3MockHosts.sh`` script in the base directory of the project.
It creates the necessary mock-hosts for the S3 tests (since S3 expects subdomains with the bucketname, e.g.
test-bucket.localhost) that are redirected to your localhost (127.0.0.1).

## Built With

* [WireMock](https://github.com/tomakehurst/wiremock) - Responsible for creating/handling the stub webservices
* [jackson-databind](https://github.com/FasterXML/jackson-databind) - Handling all the JSON and XML stuff
* [AWS-SDK](https://github.com/aws/aws-sdk-java) - UnitStack is tested against the latest version of the AWS SDK

## Contributing

Please read [CONTRIBUTING.md](https://github.com/daflockinger/unitstack/blob/master/CONTRIBUTING.md) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

[SemVer](http://semver.org/) is used for versioning. 

## Authors

* **Florian Mitterbauer** - *Initial work* - [daflockinger](https://github.com/daflockinger)


## License

This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/daflockinger/unitstack/blob/master/LICENSE) file for details

## Acknowledgments
The project was very much inspired by [LocalStack](https://github.com/localstack/localstack) and my need for a simple and fast 
tool to unit test AWS implementations without the hassle of connecting it to the real AWS services for testing.
