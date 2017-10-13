package com.flockinger.unitstack.model.s3.dto;

import java.util.UUID;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.flockinger.unitstack.response.s3.MultipartUploadResponder;

@JacksonXmlRootElement(localName=MultipartUploadResponder.MULTIPART_OP_NAME_PLACEHOLDER + "Result", 
namespace="http://s3.amazonaws.com/doc/2006-03-01/")
public class MultipartResult {
  
  @JacksonXmlProperty(localName="Bucket")
  private String bucketName;
  
  @JacksonXmlProperty(localName="Key")
  private String key;
  
  @JacksonXmlProperty(localName="UploadId")
  private String uploadId;
  
  
  public MultipartResult() {}
  
  public MultipartResult(String bucketName, String key) {
    this.bucketName = bucketName;
    this.key = key;
    this.uploadId = UUID.randomUUID().toString();
  }

  public String getBucketName() {
    return bucketName;
  }

  public void setBucketName(String bucketName) {
    this.bucketName = bucketName;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getUploadId() {
    return uploadId;
  }

  public void setUploadId(String uploadId) {
    this.uploadId = uploadId;
  }
}
