package com.flockinger.unitstack.model.s3.dto;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElementWrapper;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.flockinger.unitstack.model.s3.Grant;

@JacksonXmlRootElement(localName="AccessControlPolicy")
public class AccessControlPolicy {
  
  @JacksonXmlProperty(localName="Owner")
  private Owner owner;

  @JacksonXmlElementWrapper(localName="AccessControlList")
  @JacksonXmlProperty(localName="Grant")
  private List<Grant> accessControllList = new ArrayList<>();
  
  
  public Owner getOwner() {
    return owner;
  }
  public void setOwner(Owner owner) {
    this.owner = owner;
  }
  public List<Grant> getAccessControllList() {
    return accessControllList;
  }
  public void setAccessControllList(List<Grant> accessControllList) {
    this.accessControllList = accessControllList;
  }
}
