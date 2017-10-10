package com.flockinger.unitstack.model.s3.dto;

import java.util.Date;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

public class ObjectSummary {
  
  @JacksonXmlProperty(localName="Key")
  private String key;
  @JacksonXmlProperty(localName="LastModified")
  private Date lastModified;
  @JacksonXmlProperty(localName="ETag")
  private String etag;
  @JacksonXmlProperty(localName="Size")
  private Integer size;
  @JacksonXmlProperty(localName="StorageClass")
  private String storageClass;
  @JacksonXmlProperty(localName="Owner")
  private Owner owner;
  
  public ObjectSummary() {}
  
  public ObjectSummary(String key, Integer size, Owner owner) {
    this.key = key;
    this.lastModified = new Date();
    this.size = size;
    this.owner = owner;
    this.storageClass = "STANDARD";
  }
  
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public Date getLastModified() {
    return lastModified;
  }
  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  public String getEtag() {
    return etag;
  }
  public void setEtag(String etag) {
    this.etag = etag;
  }
  public Integer getSize() {
    return size;
  }
  public void setSize(Integer size) {
    this.size = size;
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
