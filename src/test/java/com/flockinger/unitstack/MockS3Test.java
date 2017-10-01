package com.flockinger.unitstack;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.BucketAccelerateConfiguration;
import com.amazonaws.services.s3.model.BucketCrossOriginConfiguration;
import com.amazonaws.services.s3.model.BucketLifecycleConfiguration;
import com.amazonaws.services.s3.model.BucketLoggingConfiguration;
import com.amazonaws.services.s3.model.BucketNotificationConfiguration;
import com.amazonaws.services.s3.model.BucketReplicationConfiguration;
import com.amazonaws.services.s3.model.BucketTaggingConfiguration;
import com.amazonaws.services.s3.model.BucketVersioningConfiguration;
import com.amazonaws.services.s3.model.BucketWebsiteConfiguration;
import com.amazonaws.services.s3.model.CanonicalGrantee;
import com.amazonaws.services.s3.model.CreateBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketRequest;
import com.amazonaws.services.s3.model.DeleteBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.DeleteObjectTaggingRequest;
import com.amazonaws.services.s3.model.EmailAddressGrantee;
import com.amazonaws.services.s3.model.GetBucketAccelerateConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketAclRequest;
import com.amazonaws.services.s3.model.GetBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketLocationRequest;
import com.amazonaws.services.s3.model.GetBucketLoggingConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.GetBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.HeadBucketResult;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.SetBucketAccelerateConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketAclRequest;
import com.amazonaws.services.s3.model.SetBucketAnalyticsConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketCrossOriginConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketInventoryConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketLifecycleConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketLoggingConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketMetricsConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketNotificationConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketPolicyRequest;
import com.amazonaws.services.s3.model.SetBucketReplicationConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketTaggingConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketVersioningConfigurationRequest;
import com.amazonaws.services.s3.model.SetBucketWebsiteConfigurationRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.flockinger.unitstack.model.MockParameters;

import wiremock.com.google.common.collect.ImmutableMap;


public class MockS3Test extends UnitStackTest {
  
  private AmazonS3 s3;
  
  @Before
  public void setup() {
    MockParameters params = new MockParameters();
    params.setMockRegion("EU");
    mockS3(params);
    
    EndpointConfiguration endpoint =
        new EndpointConfiguration(UNIT_STACK_URL + ":" + S3_PORT, Region.EU_Frankfurt.name());
    AWSCredentials credentials = new BasicAWSCredentials("key", "secret");
    AWSCredentialsProvider credentialsProvider = new AWSStaticCredentialsProvider(credentials);

    s3 = AmazonS3ClientBuilder.standard().withEndpointConfiguration(endpoint)
        .withCredentials(credentialsProvider).build();
  }
  
  @Test
  public void testCreateExistsListLocateAndDeleteBucket_shouldDoAllThatStuffWell() {
    String steelBucket = "stainless-steel-bucket";
    // create bucket
    Bucket bucket = s3.createBucket(new CreateBucketRequest(steelBucket));
    assertEquals("verify bucket ","stainless-steel-bucket",bucket.getName());
    
    // exists?
    boolean doesBucketExist = s3.doesBucketExist(steelBucket);
    assertTrue("verify that real bucket exists", doesBucketExist);
    assertFalse("verify that fake bucket doesn't exist", s3.doesBucketExist("nonExistante"));
    
    // list buckets
    List<Bucket> buckets = s3.listBuckets(new ListBucketsRequest());
    assertNotNull("verify list bucket returned ok", buckets);
    assertEquals("check bucket amount",1,buckets.size());
    assertEquals("check for correct bucket in list with name", steelBucket, buckets.get(0).getName());
    
    // check bucket location
    String bucketLocation = s3.getBucketLocation(new GetBucketLocationRequest(steelBucket));
    assertEquals("verify mocked bucket location", "EU", bucketLocation);
    
    // delete bucket
    s3.deleteBucket(new DeleteBucketRequest(steelBucket));
    assertFalse("verify that bucket is removed", s3.doesBucketExist(steelBucket)); 
  }
  
  @Test
  public void testBucketExist_withNotExisting_shouldReturnFalse() {
    assertFalse("verify unknown bucket doesn't exist", s3.doesBucketExist("non Existante"));
  }
  
  @Test
  public void testListBuckets_whenTheresNone_shouldReturnEmpty() {
    assertTrue("verify list of empty buckets return empty", s3.listBuckets().isEmpty());
  }
  
  @Test
  public void testDeleteBucket_thatDoesntExist_shouldNotReturnBad() {
    s3.deleteBucket("non existante");
  }
  
  @Test
  public void testSetGetBucketAcl_shouldWork() {
    String tinBucket = "tin-bucket";
    // create bucket
    Bucket bucket = s3.createBucket(new CreateBucketRequest(tinBucket));
    assertEquals("verify bucket ",tinBucket,bucket.getName());
   
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    acl.grantAllPermissions(new Grant(new CanonicalGrantee("235325u"), Permission.FullControl));
    acl.grantAllPermissions(new Grant(new EmailAddressGrantee("super@gmx.net"), Permission.Read));
    
    // set ACL
    s3.setBucketAcl(tinBucket, acl);
    // get ACL
    AccessControlList respondedAcl = s3.getBucketAcl(new GetBucketAclRequest(tinBucket));
    Grant firstGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.FullControl)).findFirst().get();
    assertEquals("verify first grant id","235325u",firstGrant.getGrantee().getIdentifier());
    Grant secondGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.Read)).findFirst().get();
    assertEquals("verify second grant id","super@gmx.net",secondGrant.getGrantee().getIdentifier());
    
    s3.deleteBucket(tinBucket);
  }
  
  @Test
  public void testGetBucketAcls_whenNoneSet_shouldWork() {
    String tinBucket = "tin-bucket";
    // create bucket
    s3.createBucket(new CreateBucketRequest(tinBucket));
    assertNotNull(s3.getBucketAcl(new GetBucketAclRequest(tinBucket)));
    s3.deleteBucket(tinBucket);
  }
  
  @Test
  public void testSetBucketAcls_withNothing_shouldWork() {
    String tinBucket = "tin-bucket";
    // create bucket
    s3.createBucket(new CreateBucketRequest(tinBucket));
    AccessControlList emptyList = new AccessControlList();
    emptyList.setOwner(new Owner("235325u","me"));
    s3.setBucketAcl(new SetBucketAclRequest(tinBucket, emptyList));
    s3.deleteBucket(tinBucket);
  }
  
  @Test
  public void testCreateGetUrlGetStringCopyExistsDeleteRestoreObject_shouldWork() {
    
  }
  
  /*
  mock good:
  *********
  
  putObject
  getUrl
  getObjectAsString
  copyObject
  doesObjectExist
  deleteObject
  restoreObject
  
  
  listObjects
  listObjectsV2
  listNextBatchOfObjects
  deleteObjects
  
  setObjectAcl
  getObjectAcl

  abortMultipartUpload
  completeMultipartUpload
  initiateMultipartUpload
  listMultipartUploads
    
  uploadPart
  listParts
  copyPart

  listVersions
  deleteVersion
  listNextBatchOfVersions
  
  generatePresignedUrl
  getS3AccountOwner
  
  
  special care for:
  *****************
  isRequesterPaysEnabled
  setBucketAnalyticsConfiguration
  setBucketCrossOriginConfiguration
  setBucketLifecycleConfiguration
  setBucketPolicy
  getObjectTagging 
  setBucketNotificationConfiguration
  setBucketReplicationConfiguration
  setBucketTaggingConfiguration
  setObjectTagging
 * */
  
  @Test
  public void testNonInjectableMocks_shouldReturnNormal() {
    String leadBucket = "lead-maybe-gold";
    assertNotNull(s3.deleteBucketAnalyticsConfiguration(new DeleteBucketAnalyticsConfigurationRequest()
        .withBucketName(leadBucket).withId("very analytic")));
    s3.deleteBucketCrossOriginConfiguration(new DeleteBucketCrossOriginConfigurationRequest(leadBucket));
    assertNotNull(s3.deleteBucketInventoryConfiguration(new DeleteBucketInventoryConfigurationRequest().withBucketName(leadBucket).withId("1")));
    s3.deleteBucketLifecycleConfiguration(new DeleteBucketLifecycleConfigurationRequest(leadBucket));
    assertNotNull(s3.deleteBucketMetricsConfiguration(new DeleteBucketMetricsConfigurationRequest().withBucketName(leadBucket).withId("1")));
    s3.deleteBucketReplicationConfiguration(new DeleteBucketReplicationConfigurationRequest(leadBucket));
    s3.deleteBucketTaggingConfiguration(new DeleteBucketTaggingConfigurationRequest(leadBucket));
    s3.deleteBucketWebsiteConfiguration(new DeleteBucketWebsiteConfigurationRequest(leadBucket));
    assertNotNull(s3.deleteObjectTagging(new DeleteObjectTaggingRequest(leadBucket, "1")));
    s3.disableRequesterPays(leadBucket);
    s3.enableRequesterPays(leadBucket);
    assertNotNull(s3.getBucketAccelerateConfiguration(new GetBucketAccelerateConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketAnalyticsConfiguration(new GetBucketAnalyticsConfigurationRequest().withBucketName(leadBucket).withId("1")));
    assertNotNull(s3.getBucketCrossOriginConfiguration(new GetBucketCrossOriginConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketInventoryConfiguration(new GetBucketInventoryConfigurationRequest().withBucketName(leadBucket).withId("1")));
    assertNotNull(s3.getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketLoggingConfiguration(new GetBucketLoggingConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketMetricsConfiguration(new GetBucketMetricsConfigurationRequest().withBucketName(leadBucket).withId("1")));
    assertNotNull(s3.getBucketNotificationConfiguration(new GetBucketNotificationConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(leadBucket)));
    assertNotNull(s3.getBucketWebsiteConfiguration(new GetBucketWebsiteConfigurationRequest(leadBucket)));
    assertNotNull(s3.listBucketAnalyticsConfigurations(new ListBucketAnalyticsConfigurationsRequest().withBucketName(leadBucket)));
    assertNotNull(s3.listBucketInventoryConfigurations(new ListBucketInventoryConfigurationsRequest().withBucketName(leadBucket)));
    assertNotNull(s3.listBucketMetricsConfigurations(new ListBucketMetricsConfigurationsRequest().withBucketName(leadBucket)));
    s3.setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(leadBucket, new BucketAccelerateConfiguration("")));
    assertNotNull(s3.setBucketInventoryConfiguration(new SetBucketInventoryConfigurationRequest().withBucketName(leadBucket).withInventoryConfiguration(new InventoryConfiguration().withId("1"))));
    s3.setBucketLoggingConfiguration(new SetBucketLoggingConfigurationRequest(leadBucket, new BucketLoggingConfiguration()));
    assertNotNull(s3.setBucketMetricsConfiguration(new SetBucketMetricsConfigurationRequest().withBucketName(leadBucket).withMetricsConfiguration(new MetricsConfiguration().withId("1"))));   
    s3.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(leadBucket, new BucketVersioningConfiguration().withStatus("ENABLED")));
    BucketWebsiteConfiguration bucketWebsiteConfig = new BucketWebsiteConfiguration();
    bucketWebsiteConfig.setIndexDocumentSuffix("1");
    s3.setBucketWebsiteConfiguration(new SetBucketWebsiteConfigurationRequest(leadBucket, bucketWebsiteConfig));
    assertNotNull(s3.headBucket(new HeadBucketRequest(leadBucket)));
    assertNotNull(s3.getBucketPolicy(leadBucket));
    s3.setBucketPolicy(new SetBucketPolicyRequest(leadBucket, "very strict policy"));
    s3.deleteBucketPolicy(leadBucket);
    assertNotNull(s3.getObjectMetadata(new GetObjectMetadataRequest(leadBucket, "23423kjh")));
  }
}
