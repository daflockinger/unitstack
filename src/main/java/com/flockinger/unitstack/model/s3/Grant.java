package com.flockinger.unitstack.model.s3;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="Grant")
public class Grant {
  
  @XmlElement(name="Grantee")
  private Grantee grantee;
  
  @XmlElement(name="Permission")
  private String permission;
  
  public String getPermission() {
    return permission;
  }
  public void setPermission(String permission) {
    this.permission = permission;
  }
}
