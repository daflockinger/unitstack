package com.flockinger.unitstack.model.sqs;

public class SqsMessage {
  private String id;
  private String receiptHandle;
  private String body;
  private String md5;
  private String batchId;
  
  public String getBatchId() {
    return batchId;
  }
  public void setBatchId(String batchId) {
    this.batchId = batchId;
  }
  public String getId() {
    return id;
  }
  public void setId(String id) {
    this.id = id;
  }
  public String getReceiptHandle() {
    return receiptHandle;
  }
  public void setReceiptHandle(String receiptHandle) {
    this.receiptHandle = receiptHandle;
  }
  public String getBody() {
    return body;
  }
  public void setBody(String body) {
    this.body = body;
  }
  public String getMd5() {
    return md5;
  }
  public void setMd5(String md5) {
    this.md5 = md5;
  }
}
