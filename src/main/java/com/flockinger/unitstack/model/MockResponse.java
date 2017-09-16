package com.flockinger.unitstack.model;

public class MockResponse {
  private int status = 200;
  private String body;
  
  public MockResponse(String body) {
    this.body = body;
  }
  
  public MockResponse(int status, String body) {
    this(body);
    this.status = status;
  }
  
  public int getStatus() {
    return status;
  }
  public void setStatus(int status) {
    this.status = status;
  }
  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }
}
