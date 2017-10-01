package com.flockinger.unitstack.model.s3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="Bucket")
public class Bucket {
  
  @JacksonXmlProperty(localName="Name")
  private String name;
 
  @JacksonXmlProperty(localName="CreationDate")
  private Date created;
  
  private List<S3Object> objects;
  
  private List<Grant> accessControllList = new ArrayList<>();
  
  
  public List<Grant> getAccessControllList() {
    return accessControllList;
  }
  public void setAccessControllList(List<Grant> accessControllList) {
    this.accessControllList = accessControllList;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }
  public List<S3Object> getObjects() {
    return objects;
  }
  public void setObjects(List<S3Object> objects) {
    this.objects = objects;
  }
}
