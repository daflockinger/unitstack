package com.flockinger.unitstack.model.s3.dto;

import java.util.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

public class PartUploadResult {

  @JacksonXmlProperty(localName="Key")
  private String key;
  @JacksonXmlProperty(localName="Initiated")
  private Date initiated;
  @JacksonXmlProperty(localName="UploadId")
  private String uploadId;
  @JacksonXmlProperty(localName="StorageClass")
  private String storageClass;
  @JacksonXmlProperty(localName="Owner")
  private Owner owner;
  
  public PartUploadResult() {}
  
  public PartUploadResult(String key, String uploadId, Owner owner) {
    this.key = key;
    this.initiated = new Date();
    this.owner = owner;
    this.storageClass = "STANDARD";
    this.uploadId = uploadId;
  }
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public String getStorageClass() {
    return storageClass;
  }
  public void setStorageClass(String storageClass) {
    this.storageClass = storageClass;
  }
  public Owner getOwner() {
    return owner;
  }
  public void setOwner(Owner owner) {
    this.owner = owner;
  }
}
