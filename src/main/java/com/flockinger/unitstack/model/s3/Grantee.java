package com.flockinger.unitstack.model.s3;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


@XmlRootElement(name="Grantee")
public class Grantee {
  
  @XmlElement(name="ID")
  private String id;
  
  @XmlElement(name="EmailAddress")
  private String emailAddress;
  
  @XmlAttribute(name="type",namespace="http://www.w3.org/2001/XMLSchema-instance")
  private String type;
  
  public String getType() {
    return type;
  }
  public void setType(String type) {
    this.type = type;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getEmailAddress() {
    return emailAddress;
  }
  public void setEmailAddress(String emailAddress) {
    this.emailAddress = emailAddress;
  }
}
