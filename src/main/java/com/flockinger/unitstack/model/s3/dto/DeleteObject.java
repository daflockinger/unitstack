package com.flockinger.unitstack.model.s3.dto;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;


@JacksonXmlRootElement(localName="Delete")
public class DeleteObject {

  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName="Object")
  private List<ObjectSummary> objects = new ArrayList<>();

  public List<ObjectSummary> getObjects() {
    return objects;
  }

  public void setObjects(List<ObjectSummary> objects) {
    this.objects = objects;
  }
}
