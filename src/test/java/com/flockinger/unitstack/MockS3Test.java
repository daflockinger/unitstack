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

import static com.amazonaws.util.StringUtils.UTF8;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
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
import com.amazonaws.services.s3.model.AmazonS3Exception;
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
import com.amazonaws.services.s3.model.CopyObjectRequest;
import com.amazonaws.services.s3.model.CopyObjectResult;
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
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectTaggingRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.DeleteObjectsResult;
import com.amazonaws.services.s3.model.DeleteVersionRequest;
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
import com.amazonaws.services.s3.model.GetObjectAclRequest;
import com.amazonaws.services.s3.model.GetObjectMetadataRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.GetObjectTaggingRequest;
import com.amazonaws.services.s3.model.Grant;
import com.amazonaws.services.s3.model.HeadBucketRequest;
import com.amazonaws.services.s3.model.HeadBucketResult;
import com.amazonaws.services.s3.model.ListBucketAnalyticsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketInventoryConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketMetricsConfigurationsRequest;
import com.amazonaws.services.s3.model.ListBucketsRequest;
import com.amazonaws.services.s3.model.ListNextBatchOfObjectsRequest;
import com.amazonaws.services.s3.model.ListNextBatchOfVersionsRequest;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ListVersionsRequest;
import com.amazonaws.services.s3.model.NotificationConfiguration;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.ObjectTagging;
import com.amazonaws.services.s3.model.Owner;
import com.amazonaws.services.s3.model.Permission;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.RedirectRule;
import com.amazonaws.services.s3.model.Region;
import com.amazonaws.services.s3.model.RestoreObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
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
import com.amazonaws.services.s3.model.SetObjectAclRequest;
import com.amazonaws.services.s3.model.SetObjectTaggingRequest;
import com.amazonaws.services.s3.model.VersionListing;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.Md5Utils;
import com.flockinger.unitstack.model.MockParameters;
import com.flockinger.unitstack.model.s3.dto.DeletedObject;
import com.flockinger.unitstack.model.s3.dto.ObjectSummary;

import wiremock.com.google.common.collect.ImmutableMap;
import wiremock.org.apache.commons.lang3.StringUtils;


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
  public void testCreateGetUrlGetStringCopyExistsDeleteObject_shouldWork() throws IOException {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    String pictureBucket = "picture-bucket";
    String backupBucket = "backup-bucket";
    String pictureKey = "unitstack.jpg";
    byte[] imageBytes = IOUtils.toByteArray(image.toURI());
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));
    s3.createBucket(new CreateBucketRequest(backupBucket));

    // upload an image
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(pictureBucket, pictureKey, image));
    assertNotNull("verify images MD5 is present", putResult.getContentMd5());
    // get URL
    URL imageUrl = s3.getUrl(pictureBucket, pictureKey);
    assertEquals("verify correct image url",("http://" + pictureBucket + ".localhost:" + S3_PORT + "/" + pictureKey)
        , imageUrl.toString());
    
    // copy image
    CopyObjectResult copyResult = s3.copyObject(new CopyObjectRequest(pictureBucket, pictureKey, backupBucket, "backup_" + pictureKey));
    assertNotNull("verify copy result is not null", copyResult);
    assertNotNull("verify last modified exists", copyResult.getLastModifiedDate());
    
    // does image copy exist
    assertTrue("verify that copying worked and check doesObjectExist", s3.doesObjectExist(backupBucket, "backup_" + pictureKey));
    // does exist with non existent
    assertFalse("verify that exists of not existing returns false", s3.doesObjectExist(backupBucket, "not_there_" + pictureKey));
    
    // download image
    S3Object downloadedImageObject = s3.getObject(new GetObjectRequest(pictureBucket, pictureKey));
    assertEquals("verify correct image bucket name", pictureBucket, downloadedImageObject.getBucketName());
    assertEquals("verify correct image key", pictureKey, downloadedImageObject.getKey());
    assertNotNull("verify metda data is not null", downloadedImageObject.getObjectMetadata());
    assertNotNull("verify image file inputstream is ok", downloadedImageObject.getObjectContent());
    byte[] downloadedImage = IOUtils.toByteArray(downloadedImageObject.getObjectContent());
    assertEquals("verify downloaded image size", imageBytes.length, downloadedImage.length);
    assertEquals("verify downloaded image MD5", getObjectMD5(imageBytes), getObjectMD5(downloadedImage));
    
    // delete image
    s3.deleteObject(new DeleteObjectRequest(pictureBucket, pictureKey));
    assertFalse("verify deleted image is gone", s3.doesObjectExist(pictureBucket, pictureKey));
  }
  
  @Test
  public void testCreateObject_withEmptyFile_shouldWork() throws IOException {
    String pictureBucket = "empty-bucket";
    String pictureKey = "empty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));
    
    File image = new File(this.getClass().getClassLoader().getResource("empty.jpg").getFile());
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(pictureBucket, pictureKey, image));
    assertNotNull(putResult);
  }
  
  @Test
  public void testGetObjectUrl_withNotExistingObject_shouldWork() throws IOException {
    String pictureBucket = "non-existant-bucket";
    String pictureKey = "whatever.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));
    
    URL imageUrl = s3.getUrl(pictureBucket, pictureKey);
    assertNotNull(imageUrl);
  }
  
  @Test
  public void testCopyImageObject_withNonExistantSourceAndTarget_shouldWork() throws IOException {
    String pictureBucket = "empty-bucket";
    String pictureKey = "empty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));

    CopyObjectResult copyResult = s3.copyObject(new CopyObjectRequest(pictureBucket, pictureKey, "backup-buckett", "backup_" + pictureKey));
    assertNotNull("verify copy result is not null", copyResult);
    assertNotNull("verify last modified exists", copyResult.getLastModifiedDate());
  }
  
  
  @Test(expected=AmazonS3Exception.class)
  public void testGetObject_withNotExistingObject_shouldThrowException() throws IOException {
    String pictureBucket = "uuempty-bucket";
    String pictureKey = "uuempty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));

    s3.getObject(new GetObjectRequest(pictureBucket, pictureKey));
  }
  
  @Test
  public void testDeleteObject_withNotExistingObject_shouldReturnNormal() throws IOException {
    String pictureBucket = "yyempty-bucket";
    String pictureKey = "yyempty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(pictureBucket));

    s3.deleteObject(new DeleteObjectRequest(pictureBucket, pictureKey));
  }
  
  
  private String getObjectMD5(byte[] objectData) {
    byte[] expectedMd5 = null;
    try {
      expectedMd5 = Md5Utils.computeMD5Hash(objectData);
    } catch (Exception e) {}
    return BinaryUtils.toHex(expectedMd5);
  }
  
  @Test
  public void testSetGetObjectAcl_shouldWork() {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    String pictureBucket = "picture-bucket";
    String pictureKey = "unitstack.jpg";
    s3.createBucket(new CreateBucketRequest(pictureBucket));
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(pictureBucket, pictureKey, image));
    assertNotNull("verify images MD5 is present", putResult.getContentMd5());
   
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    acl.grantAllPermissions(new Grant(new CanonicalGrantee("235325u"), Permission.FullControl));
    acl.grantAllPermissions(new Grant(new EmailAddressGrantee("super@gmx.net"), Permission.Read));
    
    // set ACL
    s3.setObjectAcl(new SetObjectAclRequest(pictureBucket, pictureKey, acl));
    // get ACL
    AccessControlList respondedAcl = s3.getObjectAcl(new GetObjectAclRequest(pictureBucket, pictureKey));
    Grant firstGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.FullControl)).findFirst().get();
    assertEquals("verify first grant id","235325u",firstGrant.getGrantee().getIdentifier());
    Grant secondGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.Read)).findFirst().get();
    assertEquals("verify second grant id","super@gmx.net",secondGrant.getGrantee().getIdentifier());
    
    s3.deleteBucket(pictureBucket);
  }
  
  @Test
  public void testGetObjectAcls_whenNoneSet_shouldWork() {
    String pictureBucket = "picture-bucket";
    String pictureKey = "unitstack.jpg";
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    s3.setObjectAcl(new SetObjectAclRequest(pictureBucket, pictureKey,acl));
  }
  
  @Test(expected=AmazonS3Exception.class)
  public void testSetObjectAcls_withNothing_shouldThrowNotFound() {
    String pictureBucket = "picture-bucket";
    String pictureKey = "unitstack.jpg";
    s3.getObjectAcl(new GetObjectAclRequest(pictureBucket, pictureKey));
  }
 
  
  @Test
  public void testGetAsStringListListV2ListNextBatchDeleteObjects_shouldWork() throws IOException {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    File text = new File(this.getClass().getClassLoader().getResource("sample.txt").getFile());
    String fileBucket = "file-bucket";
    String pictureKey = "unitstack.jpg";
    String textKey = "someTextFile.jpg";
    byte[] imageBytes = IOUtils.toByteArray(image.toURI());
    byte[] textBytes = IOUtils.toByteArray(text.toURI());
    s3.createBucket(new CreateBucketRequest(fileBucket));
    s3.putObject(new PutObjectRequest(fileBucket, pictureKey, image));
    s3.putObject(new PutObjectRequest(fileBucket, textKey, text));
    
    // download as String
    String resultText = s3.getObjectAsString(fileBucket, textKey);
    assertEquals("verify that downloaded text is correct",new String(textBytes,StandardCharsets.UTF_8),resultText);

    // list objects with prefix
    ObjectListing units = s3.listObjects(new ListObjectsRequest().withBucketName(fileBucket).withPrefix("unit"));
    assertNotNull("verify some listing is returned", units);
    assertEquals("verify units size", 1, units.getObjectSummaries().size());
    assertEquals("verify image key",pictureKey, units.getObjectSummaries().get(0).getKey());
    assertEquals("verify image's bucket", fileBucket, units.getObjectSummaries().get(0).getBucketName());
    assertNotNull("verify image last modified exists", units.getObjectSummaries().get(0).getLastModified());
    assertNotNull("verify image owner exists", units.getObjectSummaries().get(0).getOwner());
    assertEquals("verify image size", imageBytes.length, units.getObjectSummaries().get(0).getSize());
    // list objects without prefix
    ObjectListing all = s3.listObjects(new ListObjectsRequest().withBucketName(fileBucket));
    List<S3ObjectSummary> allSummaries = all.getObjectSummaries();
    assertEquals("verify all size", 2, allSummaries.size());
    
    // list v2 limited result-set
    ListObjectsV2Result list2LimitedResult = s3.listObjectsV2(new ListObjectsV2Request().withBucketName(fileBucket).withMaxKeys(1));
    assertEquals("verify v2 limiting result size works ",1,list2LimitedResult.getObjectSummaries().size()); 
    // list v2 prefixed
    ListObjectsV2Result list2Prefixed = s3.listObjectsV2(new ListObjectsV2Request().withBucketName(fileBucket).withPrefix("some"));
    assertEquals("verify v2 prefixed result size",1,list2Prefixed.getObjectSummaries().size());
    assertEquals("verify text key",textKey, list2Prefixed.getObjectSummaries().get(0).getKey());
    assertEquals("verify text bucket", fileBucket, list2Prefixed.getObjectSummaries().get(0).getBucketName());
    assertNotNull("verify text last modified exists", list2Prefixed.getObjectSummaries().get(0).getLastModified());
    assertEquals("verify text size", textBytes.length, list2Prefixed.getObjectSummaries().get(0).getSize());
    
    // list next batch 
    ObjectListing allSmall = s3.listObjects(new ListObjectsRequest().withBucketName(fileBucket).withMaxKeys(1));
    ObjectListing nextSmall = s3.listNextBatchOfObjects(new ListNextBatchOfObjectsRequest(allSmall));
    assertEquals("verify next batch bucket name", fileBucket, nextSmall.getBucketName());
    assertEquals("verify next batch max keys", 1, nextSmall.getMaxKeys());
    assertEquals("verify next batch summaries size", 1, nextSmall.getObjectSummaries().size());
    assertEquals("verify next batch key", textKey, nextSmall.getObjectSummaries().get(0).getKey());
   
    // delete objects specific
    DeleteObjectsResult deleteResultSpecific = s3.deleteObjects(new DeleteObjectsRequest(fileBucket).withKeys(pictureKey));
    assertEquals("verify delete result contains object count", 1, deleteResultSpecific.getDeletedObjects().size());
    assertEquals("verify delete result contains deleted object key", pictureKey, deleteResultSpecific.getDeletedObjects().get(0).getKey());
    assertEquals("after specific deletion the right one's left", textKey, s3.listObjects(fileBucket).getObjectSummaries().get(0).getKey());
    // recreate for next deletion
    s3.putObject(new PutObjectRequest(fileBucket, textKey, text));
   
    // delete objects all
    DeleteObjectsResult deleteResultAll = s3.deleteObjects(new DeleteObjectsRequest(fileBucket));
    assertEquals("verify delete result contains object count", 2, deleteResultAll.getDeletedObjects().size());
    assertTrue("verify delete result contains all deleted object keys", deleteResultAll.getDeletedObjects().stream()
        .map(com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject::getKey)
    .allMatch(key -> StringUtils.containsAny(key, pictureKey,textKey)));
    assertEquals("after specific deletion no object's left", 0, s3.listObjects(fileBucket).getObjectSummaries().size());
  }
  
  //TODO add second test to ListListV2ListNextBatchDeleteObjects
  
  @Test  //TODO and then continue with this
  public void testInitAbortCompleteListMultipartUpload_shouldWork() {
    
  }
  
  /*
  mock good:
  *********  
  
  abortMultipartUpload
  completeMultipartUpload
  initiateMultipartUpload
  listMultipartUploads
    
  uploadPart
  listParts
  copyPart
  
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
    try {
      assertNotNull(s3.getObjectMetadata(new GetObjectMetadataRequest(leadBucket, "23423kjh")));
    } catch(AmazonS3Exception s3Exception) {
      assertEquals("should return not found", 404,s3Exception.getStatusCode());
    }
    s3.restoreObject(new RestoreObjectRequest(leadBucket, "bullet.jpg").withExpirationInDays(23));
    // TODO fix that assertNotNull(s3.listVersions(new ListVersionsRequest().withBucketName(leadBucket)));
    s3.deleteVersion(new DeleteVersionRequest(leadBucket, "bullet.exe", "v3"));
    assertNotNull(s3.listNextBatchOfVersions(new ListNextBatchOfVersionsRequest(new VersionListing())));
  }
}
