package com.flockinger.unitstack.model.sns;

public class SnsMessage {
  private String id;
  private String body;
  private String structure;
  private String subject;
  
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }
  public String getStructure() {
    return structure;
  }
  public void setStructure(String structure) {
    this.structure = structure;
  }
  public String getSubject() {
    return subject;
  }
  public void setSubject(String subject) {
    this.subject = subject;
  }
}
