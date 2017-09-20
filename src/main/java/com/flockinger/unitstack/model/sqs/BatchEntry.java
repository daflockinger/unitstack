package com.flockinger.unitstack.model.sqs;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BatchEntry {
  
  @JsonProperty("Id")
  private String id;
  @JsonProperty("MessageBody")
  private String body;
  @JsonProperty("MessageGroupId")
  private String groupId;
  @JsonProperty("DelaySeconds")
  private String delaySeconds;
  @JsonProperty("ReceiptHandle")
  private String receiptHandle;

  public String getReceiptHandle() {
    return receiptHandle;
  }
  public void setReceiptHandle(String receiptHandle) {
    this.receiptHandle = receiptHandle;
  }
  public String getId() {
    return id;
  }
  public String getBody() {
    return body;
  }
  public String getGroupId() {
    return groupId;
  }
  public String getDelaySeconds() {
    return delaySeconds;
  }
  public void setId(String id) {
    this.id = id;
  }
  public void setBody(String body) {
    this.body = body;
  }
  public void setGroupId(String groupId) {
    this.groupId = groupId;
  }
  public void setDelaySeconds(String delaySeconds) {
    this.delaySeconds = delaySeconds;
  }
}
