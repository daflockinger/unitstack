package com.flockinger.unitstack.model.s3;

public class S3Part {
  
  private byte[] partBytes;
  private String key;
  private String uploadId;
 
  public S3Part(byte[] partBytes, String key, String uploadId) {
    super();
    this.partBytes = partBytes;
    this.key = key;
    this.uploadId = uploadId;
  }
  
  public byte[] getPartBytes() {
    return partBytes;
  }
  public void setPartBytes(byte[] partBytes) {
    this.partBytes = partBytes;
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
