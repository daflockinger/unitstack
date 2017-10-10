package com.flockinger.unitstack.model.s3.dto;

import java.util.UUID;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="Deleted")
public class DeletedObject {

  @JacksonXmlProperty(localName="Key")
  private String key;
  
  @JacksonXmlProperty(localName="VersionId")
  private String versionId;
  
  @JacksonXmlProperty(localName="DeleteMarker")
  private String deleteMarker;
  
  @JacksonXmlProperty(localName="DeleteMarkerVersionId")
  private String deleteMarkerVersionId;

  public DeletedObject(String key) {
    this.key = key;
    this.versionId = UUID.randomUUID().toString();
    this.deleteMarkerVersionId = UUID.randomUUID().toString();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public String getDeleteMarker() {
    return deleteMarker;
  }

  public void setDeleteMarker(String deleteMarker) {
    this.deleteMarker = deleteMarker;
  }

  public String getDeleteMarkerVersionId() {
    return deleteMarkerVersionId;
  }

  public void setDeleteMarkerVersionId(String deleteMarkerVersionId) {
    this.deleteMarkerVersionId = deleteMarkerVersionId;
  }
}
