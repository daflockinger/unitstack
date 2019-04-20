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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder.EndpointConfiguration;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.model.analytics.AnalyticsConfiguration;
import com.amazonaws.services.s3.model.inventory.InventoryConfiguration;
import com.amazonaws.services.s3.model.metrics.MetricsConfiguration;
import com.amazonaws.util.BinaryUtils;
import com.amazonaws.util.Md5Utils;
import com.flockinger.unitstack.model.MockParameters;

import wiremock.org.apache.commons.lang3.StringUtils;


public class MockS3Test extends UnitStackTest {
  
  private AmazonS3 s3;
  
  private final static String MOCK_BUCKET_1 = "mockbucket1";
  private final static String MOCK_BUCKET_2 = "mockbucket2";
  private final static String MOCK_BUCKET_3 = "mockbucket3";
  
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

  @After
  public void teardown() {
    s3.deleteBucket(MOCK_BUCKET_1);
    s3.deleteBucket(MOCK_BUCKET_2);
    s3.deleteBucket(MOCK_BUCKET_3);
  }

  @Test
  public void testCreateExistsListLocateAndDeleteBucket_shouldDoAllThatStuffWell() {
    // create bucket
    Bucket bucket = s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    assertEquals("verify bucket ",MOCK_BUCKET_1,bucket.getName());
    
    // exists?
    boolean doesBucketExist = s3.doesBucketExistV2(MOCK_BUCKET_1);
    assertTrue("verify that real bucket exists", doesBucketExist);
    assertFalse("verify that fake bucket doesn't exist", s3.doesBucketExistV2("nonExistante"));
    
    // list buckets
    List<Bucket> buckets = s3.listBuckets(new ListBucketsRequest());
    assertNotNull("verify list bucket returned ok", buckets);
    assertEquals("check bucket amount",1,buckets.size());
    assertEquals("check for correct bucket in list with name", MOCK_BUCKET_1, buckets.get(0).getName());
    
    // check bucket location
    String bucketLocation = s3.getBucketLocation(new GetBucketLocationRequest(MOCK_BUCKET_1));
    assertEquals("verify mocked bucket location", "EU", bucketLocation);
    
    // delete bucket
    s3.deleteBucket(new DeleteBucketRequest(MOCK_BUCKET_1));
    assertFalse("verify that bucket is removed", s3.doesBucketExistV2(MOCK_BUCKET_1)); 
  }
  
  @Test
  public void testBucketExist_withNotExisting_shouldReturnFalse() {
    assertFalse("verify unknown bucket doesn't exist", s3.doesBucketExistV2("nonExistante"));
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
    // create bucket
    Bucket bucket = s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    assertEquals("verify bucket ",MOCK_BUCKET_1,bucket.getName());
   
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    acl.grantAllPermissions(new Grant(new CanonicalGrantee("235325u"), Permission.FullControl));
    acl.grantAllPermissions(new Grant(new EmailAddressGrantee("super@gmx.net"), Permission.Read));
    
    // set ACL
    s3.setBucketAcl(MOCK_BUCKET_1, acl);
    // get ACL
    AccessControlList respondedAcl = s3.getBucketAcl(new GetBucketAclRequest(MOCK_BUCKET_1));
    Grant firstGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.FullControl)).findFirst().get();
    assertEquals("verify first grant id","235325u",firstGrant.getGrantee().getIdentifier());
    Grant secondGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.Read)).findFirst().get();
    assertEquals("verify second grant id","super@gmx.net",secondGrant.getGrantee().getIdentifier());
    
    s3.deleteBucket(MOCK_BUCKET_1);
  }
  
  @Test
  public void testGetBucketAcls_whenNoneSet_shouldWork() {
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    assertNotNull(s3.getBucketAcl(new GetBucketAclRequest(MOCK_BUCKET_1)));
    s3.deleteBucket(MOCK_BUCKET_1);
  }
  
  @Test
  public void testSetBucketAcls_withNothing_shouldWork() {
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    AccessControlList emptyList = new AccessControlList();
    emptyList.setOwner(new Owner("235325u","me"));
    s3.setBucketAcl(new SetBucketAclRequest(MOCK_BUCKET_1, emptyList));
    s3.deleteBucket(MOCK_BUCKET_1);
  }
  
  @Test
  public void testCreateGetUrlGetStringCopyExistsDeleteObject_shouldWork() throws IOException {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    String pictureKey = "unitstack.jpg";
    byte[] imageBytes = IOUtils.toByteArray(image.toURI());
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_2));

    // upload an image
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, pictureKey, image));
    assertNotNull("verify images MD5 is present", putResult.getContentMd5());
    // get URL
    URL imageUrl = s3.getUrl(MOCK_BUCKET_1, pictureKey);
    assertEquals("verify correct image url",("http://" + MOCK_BUCKET_1 + ".localhost:" + S3_PORT + "/" + pictureKey)
        , imageUrl.toString());
    
    // copy image
    CopyObjectResult copyResult = s3.copyObject(new CopyObjectRequest(MOCK_BUCKET_1, pictureKey, MOCK_BUCKET_2, "backup_" + pictureKey));
    assertNotNull("verify copy result is not null", copyResult);
    assertNotNull("verify last modified exists", copyResult.getLastModifiedDate());
    
    // does image copy exist
    assertTrue("verify that copying worked and check doesObjectExist", s3.doesObjectExist(MOCK_BUCKET_2, "backup_" + pictureKey));
    // does exist with non existent
    assertFalse("verify that exists of not existing returns false", s3.doesObjectExist(MOCK_BUCKET_2, "not_there_" + pictureKey));
    
    // download image
    S3Object downloadedImageObject = s3.getObject(new GetObjectRequest(MOCK_BUCKET_1, pictureKey));
    assertEquals("verify correct image bucket name", MOCK_BUCKET_1, downloadedImageObject.getBucketName());
    assertEquals("verify correct image key", pictureKey, downloadedImageObject.getKey());
    assertNotNull("verify metda data is not null", downloadedImageObject.getObjectMetadata());
    assertNotNull("verify image file inputstream is ok", downloadedImageObject.getObjectContent());
    byte[] downloadedImage = IOUtils.toByteArray(downloadedImageObject.getObjectContent());
    assertEquals("verify downloaded image size", imageBytes.length, downloadedImage.length);
    assertEquals("verify downloaded image MD5", getObjectMD5(imageBytes), getObjectMD5(downloadedImage));
    
    // delete image
    s3.deleteObject(new DeleteObjectRequest(MOCK_BUCKET_1, pictureKey));
    assertFalse("verify deleted image is gone", s3.doesObjectExist(MOCK_BUCKET_1, pictureKey));
  }
  
  @Test
  public void testCreateObject_withEmptyFile_shouldWork() throws IOException {
    String pictureKey = "empty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    
    File image = new File(this.getClass().getClassLoader().getResource("empty.jpg").getFile());
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, pictureKey, image));
    assertNotNull(putResult);
  }
  
  @Test
  public void testGetObjectUrl_withNotExistingObject_shouldWork() throws IOException {
    String pictureKey = "whatever.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    
    URL imageUrl = s3.getUrl(MOCK_BUCKET_1, pictureKey);
    assertNotNull(imageUrl);
  }
  
  @Test
  public void testCopyImageObject_withNonExistantSourceAndTarget_shouldWork() throws IOException {
    String pictureKey = "empty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));

    CopyObjectResult copyResult = s3.copyObject(new CopyObjectRequest(MOCK_BUCKET_1, pictureKey, MOCK_BUCKET_3, "backup_" + pictureKey));
    assertNotNull("verify copy result is not null", copyResult);
    assertNotNull("verify last modified exists", copyResult.getLastModifiedDate());
  }
  
  
  @Test(expected=AmazonS3Exception.class)
  public void testGetObject_withNotExistingObject_shouldThrowException() throws IOException {
    String pictureKey = "uuempty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));

    s3.getObject(new GetObjectRequest(MOCK_BUCKET_1, pictureKey));
  }
  
  @Test
  public void testDeleteObject_withNotExistingObject_shouldReturnNormal() throws IOException {
    String pictureKey = "yyempty.jpg";
    // create bucket
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));

    s3.deleteObject(new DeleteObjectRequest(MOCK_BUCKET_1, pictureKey));
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
    String pictureKey = "unitstack.jpg";
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    PutObjectResult putResult = s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, pictureKey, image));
    assertNotNull("verify images MD5 is present", putResult.getContentMd5());
   
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    acl.grantAllPermissions(new Grant(new CanonicalGrantee("235325u"), Permission.FullControl));
    acl.grantAllPermissions(new Grant(new EmailAddressGrantee("super@gmx.net"), Permission.Read));
    
    // set ACL
    s3.setObjectAcl(new SetObjectAclRequest(MOCK_BUCKET_1, pictureKey, acl));
    // get ACL
    AccessControlList respondedAcl = s3.getObjectAcl(new GetObjectAclRequest(MOCK_BUCKET_1, pictureKey));
    Grant firstGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.FullControl)).findFirst().get();
    assertEquals("verify first grant id","235325u",firstGrant.getGrantee().getIdentifier());
    Grant secondGrant = respondedAcl.getGrantsAsList().stream().filter(grant -> grant.getPermission().equals(Permission.Read)).findFirst().get();
    assertEquals("verify second grant id","super@gmx.net",secondGrant.getGrantee().getIdentifier());
    
    s3.deleteBucket(MOCK_BUCKET_1);
  }
  
  @Test
  public void testGetObjectAcls_whenNoneSet_shouldWork() {
    String pictureKey = "unitstack.jpg";
    AccessControlList acl = new AccessControlList();
    acl.setOwner(new Owner("235325u","me"));
    s3.setObjectAcl(new SetObjectAclRequest(MOCK_BUCKET_1, pictureKey,acl));
  }
  
  @Test(expected=AmazonS3Exception.class)
  public void testSetObjectAcls_withNothing_shouldThrowNotFound() {
    String pictureKey = "unitstack.jpg";
    s3.getObjectAcl(new GetObjectAclRequest(MOCK_BUCKET_1, pictureKey));
  }
 
  
  @Test
  public void testGetAsStringListListV2ListNextBatchDeleteObjects_shouldWork() throws IOException {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    File text = new File(this.getClass().getClassLoader().getResource("sample.txt").getFile());
    String pictureKey = "unitstack.jpg";
    String textKey = "someTextFile.jpg";
    byte[] imageBytes = IOUtils.toByteArray(image.toURI());
    byte[] textBytes = IOUtils.toByteArray(text.toURI());
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, pictureKey, image));
    s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, textKey, text));
    
    // download as String
    String resultText = s3.getObjectAsString(MOCK_BUCKET_1, textKey);
    assertEquals("verify that downloaded text is correct",new String(textBytes,StandardCharsets.UTF_8),resultText);

    // list objects with prefix
    ObjectListing units = s3.listObjects(new ListObjectsRequest().withBucketName(MOCK_BUCKET_1).withPrefix("unit"));
    assertNotNull("verify some listing is returned", units);
    assertEquals("verify units size", 1, units.getObjectSummaries().size());
    assertEquals("verify image key",pictureKey, units.getObjectSummaries().get(0).getKey());
    assertEquals("verify image's bucket", MOCK_BUCKET_1, units.getObjectSummaries().get(0).getBucketName());
    assertNotNull("verify image last modified exists", units.getObjectSummaries().get(0).getLastModified());
    assertNotNull("verify image owner exists", units.getObjectSummaries().get(0).getOwner());
    assertEquals("verify image size", imageBytes.length, units.getObjectSummaries().get(0).getSize());
    // list objects without prefix
    ObjectListing all = s3.listObjects(new ListObjectsRequest().withBucketName(MOCK_BUCKET_1));
    List<S3ObjectSummary> allSummaries = all.getObjectSummaries();
    assertEquals("verify all size", 2, allSummaries.size());
    
    // list v2 limited result-set
    ListObjectsV2Result list2LimitedResult = s3.listObjectsV2(new ListObjectsV2Request().withBucketName(MOCK_BUCKET_1).withMaxKeys(1));
    assertEquals("verify v2 limiting result size works ",1,list2LimitedResult.getObjectSummaries().size()); 
    // list v2 prefixed
    ListObjectsV2Result list2Prefixed = s3.listObjectsV2(new ListObjectsV2Request().withBucketName(MOCK_BUCKET_1).withPrefix("some"));
    assertEquals("verify v2 prefixed result size",1,list2Prefixed.getObjectSummaries().size());
    assertEquals("verify text key",textKey, list2Prefixed.getObjectSummaries().get(0).getKey());
    assertEquals("verify text bucket", MOCK_BUCKET_1, list2Prefixed.getObjectSummaries().get(0).getBucketName());
    assertNotNull("verify text last modified exists", list2Prefixed.getObjectSummaries().get(0).getLastModified());
    assertEquals("verify text size", textBytes.length, list2Prefixed.getObjectSummaries().get(0).getSize());
    
    // list next batch 
    ObjectListing allSmall = s3.listObjects(new ListObjectsRequest().withBucketName(MOCK_BUCKET_1).withMaxKeys(1));
    ObjectListing nextSmall = s3.listNextBatchOfObjects(new ListNextBatchOfObjectsRequest(allSmall));
    assertEquals("verify next batch bucket name", MOCK_BUCKET_1, nextSmall.getBucketName());
    assertEquals("verify next batch max keys", 1, nextSmall.getMaxKeys());
    assertEquals("verify next batch summaries size", 1, nextSmall.getObjectSummaries().size());
    assertEquals("verify next batch key", textKey, nextSmall.getObjectSummaries().get(0).getKey());
   
    // delete objects specific
    DeleteObjectsResult deleteResultSpecific = s3.deleteObjects(new DeleteObjectsRequest(MOCK_BUCKET_1).withKeys(pictureKey));
    assertEquals("verify delete result contains object count", 1, deleteResultSpecific.getDeletedObjects().size());
    assertEquals("verify delete result contains deleted object key", pictureKey, deleteResultSpecific.getDeletedObjects().get(0).getKey());
    assertEquals("after specific deletion the right one's left", textKey, s3.listObjects(MOCK_BUCKET_1).getObjectSummaries().get(0).getKey());
    // recreate for next deletion
    s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, textKey, text));
   
    // delete objects all
    DeleteObjectsResult deleteResultAll = s3.deleteObjects(new DeleteObjectsRequest(MOCK_BUCKET_1));
    assertEquals("verify delete result contains object count", 2, deleteResultAll.getDeletedObjects().size());
    assertTrue("verify delete result contains all deleted object keys", deleteResultAll.getDeletedObjects().stream()
        .map(com.amazonaws.services.s3.model.DeleteObjectsResult.DeletedObject::getKey)
    .allMatch(key -> StringUtils.containsAny(key, pictureKey,textKey)));
    assertEquals("after specific deletion no object's left", 0, s3.listObjects(MOCK_BUCKET_1).getObjectSummaries().size());
  }

  @Test
  public void testInitUploadListPartAbortCompleteListMultipartUpload_shouldWork() throws IOException {
    File image = new File(this.getClass().getClassLoader().getResource("test.jpg").getFile());
    File text = new File(this.getClass().getClassLoader().getResource("sample.txt").getFile());
    String pictureKey = "unitstack.jpg";
    String textKey = "someTextFile.jpg";
    byte[] imageBytes = IOUtils.toByteArray(image.toURI());
    s3.createBucket(new CreateBucketRequest(MOCK_BUCKET_1));
    s3.putObject(new PutObjectRequest(MOCK_BUCKET_1, textKey, text));
    int partLength = imageBytes.length/2;

    // init multipart upload
    InitiateMultipartUploadResult initResult = s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(MOCK_BUCKET_1, pictureKey));
    assertEquals("verify correct initmultipart bucket name", MOCK_BUCKET_1, initResult.getBucketName());
    assertEquals("verify correct initmultipart key", pictureKey, initResult.getKey());
    assertNotNull("verify existing initmultipart uploadId",initResult.getUploadId());
    // upload first part
    UploadPartResult upPartResult = s3.uploadPart(new UploadPartRequest().withBucketName(MOCK_BUCKET_1).withKey(pictureKey)
        .withFile(image).withPartNumber(1).withPartSize(partLength).withFile(image).withUploadId(initResult.getUploadId()));
    assertEquals("verify part number", 1, upPartResult.getPartNumber());
    // abort upload
    s3.abortMultipartUpload(new AbortMultipartUploadRequest(MOCK_BUCKET_1, pictureKey, initResult.getUploadId()));
    assertFalse("should be gone after part upload aborted", s3.doesObjectExist(MOCK_BUCKET_1, pictureKey));
    
    // second try
    InitiateMultipartUploadResult secondInitResult = s3.initiateMultipartUpload(new InitiateMultipartUploadRequest(MOCK_BUCKET_1, pictureKey));
    // upload first part again
    UploadPartResult upPartResultAgain =s3.uploadPart(new UploadPartRequest().withBucketName(MOCK_BUCKET_1).withKey(pictureKey)
        .withFile(image).withPartNumber(1).withPartSize(partLength).withFile(image).withUploadId(secondInitResult.getUploadId()));
    assertEquals("verify part number", 1, upPartResultAgain.getPartNumber());
    // upload second part
    UploadPartResult secondPartResult = s3.uploadPart(new UploadPartRequest().withBucketName(MOCK_BUCKET_1).withKey(pictureKey)
        .withFile(image).withPartNumber(2).withPartSize(imageBytes.length - partLength).withFile(image).withUploadId(secondInitResult.getUploadId()));
    assertEquals("verify second part number", 2, secondPartResult.getPartNumber());
    
    // list ongoing multipart uploads
    MultipartUploadListing multiListing = s3.listMultipartUploads(new ListMultipartUploadsRequest(MOCK_BUCKET_1).withPrefix("unit").withMaxUploads(1));
    assertEquals("verify correct multi-part listing bucket name", MOCK_BUCKET_1, multiListing.getBucketName());
    assertEquals("verify correct multi-part listing max uploads", 1, multiListing.getMaxUploads());
    assertEquals("verify correct multi-part listing prefix", "unit", multiListing.getPrefix());
    assertEquals("verify correct multi-part listing part count", 1, multiListing.getMultipartUploads().size());
    assertEquals("verify correct multi-part listing first part key", pictureKey + ".1", multiListing.getMultipartUploads().get(0).getKey());

    // complete multipart upload
    CompleteMultipartUploadResult completeResult = s3.completeMultipartUpload(new CompleteMultipartUploadRequest()
        .withBucketName(MOCK_BUCKET_1).withKey(pictureKey).withPartETags(upPartResultAgain,secondPartResult)
        .withUploadId(secondInitResult.getUploadId()));
    assertEquals("verify correct multi-part complete bucket name", MOCK_BUCKET_1, completeResult.getBucketName());
    assertEquals("verify correct multi-part complete key", pictureKey, completeResult.getKey());
    byte[] completedDownloaded = IOUtils.toByteArray(s3.getObject(new GetObjectRequest(MOCK_BUCKET_1, pictureKey)).getObjectContent());
    assertEquals("verify completed assembled multipart size", imageBytes.length, completedDownloaded.length);
    
    // copy object to part
    CopyPartResult copyResult = s3.copyPart(new CopyPartRequest().withDestinationBucketName(MOCK_BUCKET_1).withDestinationKey(pictureKey)
        .withFirstByte(0l).withLastByte(0l).withPartNumber(2).withSourceBucketName(MOCK_BUCKET_1).withSourceKey(textKey)
        .withUploadId(secondInitResult.getUploadId()));
    assertNotNull("verify copy result is ok", copyResult);
  }
    
  @Test
  public void testEvilNonInjectableMocks_shouldReturnNormal() {
    s3.isRequesterPaysEnabled(MOCK_BUCKET_3);
    
    assertNotNull(s3.setBucketAnalyticsConfiguration(new SetBucketAnalyticsConfigurationRequest()
        .withBucketName(MOCK_BUCKET_3).withAnalyticsConfiguration(new AnalyticsConfiguration().withId("sdf"))));
    s3.setBucketCrossOriginConfiguration(new SetBucketCrossOriginConfigurationRequest(MOCK_BUCKET_3, 
        new BucketCrossOriginConfiguration().withRules(new CORSRule().withId("dfsf"))));
    s3.setBucketLifecycleConfiguration(new SetBucketLifecycleConfigurationRequest(MOCK_BUCKET_3, 
        new BucketLifecycleConfiguration().withRules(new BucketLifecycleConfiguration.Rule())));
    s3.setBucketPolicy(new SetBucketPolicyRequest(MOCK_BUCKET_3, "this policy must not be clean"));
    s3.setBucketNotificationConfiguration(new SetBucketNotificationConfigurationRequest(
        MOCK_BUCKET_3, new BucketNotificationConfiguration()));
    s3.setBucketReplicationConfiguration(new SetBucketReplicationConfigurationRequest().withBucketName(MOCK_BUCKET_3)
        .withReplicationConfiguration(new BucketReplicationConfiguration()));
    s3.setBucketTaggingConfiguration(new SetBucketTaggingConfigurationRequest(MOCK_BUCKET_3, new BucketTaggingConfiguration().withTagSets(new TagSet())));
    
    assertNotNull(s3.setObjectTagging(new SetObjectTaggingRequest(MOCK_BUCKET_3, "sdfdsf", new ObjectTagging(new ArrayList<>()))));
    assertNotNull(s3.getObjectTagging(new GetObjectTaggingRequest(MOCK_BUCKET_3, "lkjlkj")));
    assertNotNull(s3.listVersions(new ListVersionsRequest().withBucketName(MOCK_BUCKET_3)));
  }
  
  @Test
  public void testNonInjectableMocks_shouldReturnNormal() {
    assertNotNull(getBuckets());
    assertNotNull(s3.deleteBucketAnalyticsConfiguration(new DeleteBucketAnalyticsConfigurationRequest()
        .withBucketName(MOCK_BUCKET_3).withId("very analytic")));
    s3.deleteBucketCrossOriginConfiguration(new DeleteBucketCrossOriginConfigurationRequest(MOCK_BUCKET_3));
    assertNotNull(s3.deleteBucketInventoryConfiguration(new DeleteBucketInventoryConfigurationRequest().withBucketName(MOCK_BUCKET_3).withId("1")));
    s3.deleteBucketLifecycleConfiguration(new DeleteBucketLifecycleConfigurationRequest(MOCK_BUCKET_3));
    assertNotNull(s3.deleteBucketMetricsConfiguration(new DeleteBucketMetricsConfigurationRequest().withBucketName(MOCK_BUCKET_3).withId("1")));
    s3.deleteBucketReplicationConfiguration(new DeleteBucketReplicationConfigurationRequest(MOCK_BUCKET_3));
    s3.deleteBucketTaggingConfiguration(new DeleteBucketTaggingConfigurationRequest(MOCK_BUCKET_3));
    s3.deleteBucketWebsiteConfiguration(new DeleteBucketWebsiteConfigurationRequest(MOCK_BUCKET_3));
    assertNotNull(s3.deleteObjectTagging(new DeleteObjectTaggingRequest(MOCK_BUCKET_3, "1")));
    s3.disableRequesterPays(MOCK_BUCKET_3);
    s3.enableRequesterPays(MOCK_BUCKET_3);
    assertNotNull(s3.getBucketAccelerateConfiguration(new GetBucketAccelerateConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketAnalyticsConfiguration(new GetBucketAnalyticsConfigurationRequest().withBucketName(MOCK_BUCKET_3).withId("1")));
    assertNotNull(s3.getBucketCrossOriginConfiguration(new GetBucketCrossOriginConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketInventoryConfiguration(new GetBucketInventoryConfigurationRequest().withBucketName(MOCK_BUCKET_3).withId("1")));
    assertNotNull(s3.getBucketLifecycleConfiguration(new GetBucketLifecycleConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketLoggingConfiguration(new GetBucketLoggingConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketMetricsConfiguration(new GetBucketMetricsConfigurationRequest().withBucketName(MOCK_BUCKET_3).withId("1")));
    assertNotNull(s3.getBucketNotificationConfiguration(new GetBucketNotificationConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketReplicationConfiguration(new GetBucketReplicationConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketTaggingConfiguration(new GetBucketTaggingConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketVersioningConfiguration(new GetBucketVersioningConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketWebsiteConfiguration(new GetBucketWebsiteConfigurationRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.listBucketAnalyticsConfigurations(new ListBucketAnalyticsConfigurationsRequest().withBucketName(MOCK_BUCKET_3)));
    assertNotNull(s3.listBucketInventoryConfigurations(new ListBucketInventoryConfigurationsRequest().withBucketName(MOCK_BUCKET_3)));
    assertNotNull(s3.listBucketMetricsConfigurations(new ListBucketMetricsConfigurationsRequest().withBucketName(MOCK_BUCKET_3)));
    s3.setBucketAccelerateConfiguration(new SetBucketAccelerateConfigurationRequest(MOCK_BUCKET_3, new BucketAccelerateConfiguration("")));
    assertNotNull(s3.setBucketInventoryConfiguration(new SetBucketInventoryConfigurationRequest().withBucketName(MOCK_BUCKET_3).withInventoryConfiguration(new InventoryConfiguration().withId("1"))));
    s3.setBucketLoggingConfiguration(new SetBucketLoggingConfigurationRequest(MOCK_BUCKET_3, new BucketLoggingConfiguration()));
    assertNotNull(s3.setBucketMetricsConfiguration(new SetBucketMetricsConfigurationRequest().withBucketName(MOCK_BUCKET_3).withMetricsConfiguration(new MetricsConfiguration().withId("1"))));   
    s3.setBucketVersioningConfiguration(new SetBucketVersioningConfigurationRequest(MOCK_BUCKET_3, new BucketVersioningConfiguration().withStatus("ENABLED")));
    BucketWebsiteConfiguration bucketWebsiteConfig = new BucketWebsiteConfiguration();
    bucketWebsiteConfig.setIndexDocumentSuffix("1");
    s3.setBucketWebsiteConfiguration(new SetBucketWebsiteConfigurationRequest(MOCK_BUCKET_3, bucketWebsiteConfig));
    assertNotNull(s3.headBucket(new HeadBucketRequest(MOCK_BUCKET_3)));
    assertNotNull(s3.getBucketPolicy(MOCK_BUCKET_3));
    s3.setBucketPolicy(new SetBucketPolicyRequest(MOCK_BUCKET_3, "very strict policy"));
    s3.deleteBucketPolicy(MOCK_BUCKET_3);
    try {
      assertNotNull(s3.getObjectMetadata(new GetObjectMetadataRequest(MOCK_BUCKET_3, "23423kjh")));
    } catch(AmazonS3Exception s3Exception) {
      assertEquals("should return not found", 404,s3Exception.getStatusCode());
    }
    s3.restoreObject(new RestoreObjectRequest(MOCK_BUCKET_3, "bullet.jpg").withExpirationInDays(23));
    
    s3.deleteVersion(new DeleteVersionRequest(MOCK_BUCKET_3, "bullet.exe", "v3"));
    assertNotNull(s3.listNextBatchOfVersions(new ListNextBatchOfVersionsRequest(new VersionListing())));
    assertNotNull(s3.generatePresignedUrl("nonExistante", "somekey", new Date()));
    assertNotNull(s3.getS3AccountOwner());
  }
}
