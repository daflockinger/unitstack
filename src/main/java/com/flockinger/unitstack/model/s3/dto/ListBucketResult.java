package com.flockinger.unitstack.model.s3.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="ListBucketResult",namespace="http://s3.amazonaws.com/doc/2006-03-01/")
public class ListBucketResult {
  
  @JacksonXmlProperty(localName="Name")
  private String name;
  @JacksonXmlProperty(localName="Prefix")
  private String prefix;
  @JacksonXmlProperty(localName="Marker")
  private String marker;
  @JacksonXmlProperty(localName="MaxKeys")
  private Integer maxKeys;
  @JacksonXmlProperty(localName="IsTruncated")
  private Boolean isTruncated = true;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName="Contents")
  private List<ObjectSummary> contents = new ArrayList<>();
  
  public ListBucketResult(String name, String prefix, Integer maxKeys, String marker) {
    super();
    this.name = name;
    this.prefix = prefix;
    this.maxKeys = maxKeys;
    this.marker = marker;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPrefix() {
    return prefix;
  }
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  public String getMarker() {
    return marker;
  }
  public void setMarker(String marker) {
    this.marker = marker;
  }
  public Integer getMaxKeys() {
    return maxKeys;
  }
  public void setMaxKeys(Integer maxKeys) {
    this.maxKeys = maxKeys;
  }
  public Boolean getIsTruncated() {
    return isTruncated;
  }
  public void setIsTruncated(Boolean isTruncated) {
    this.isTruncated = isTruncated;
  }
  public List<ObjectSummary> getContents() {
    return contents;
  }
  public void setContents(List<ObjectSummary> contents) {
    this.contents = contents;
  }
}
