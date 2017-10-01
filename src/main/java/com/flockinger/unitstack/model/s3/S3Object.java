package com.flockinger.unitstack.model.s3;

import java.util.List;

public class S3Object {
  private String key;
  private byte[] objectData;

  private List<Grant> accessControllList;

  public List<Grant> getAccessControllList() {
    return accessControllList;
  }
  public void setAccessControllList(List<Grant> accessControllList) {
    this.accessControllList = accessControllList;
  }
  public String getKey() {
    return key;
  }
  public void setKey(String key) {
    this.key = key;
  }
  public byte[] getObjectData() {
    return objectData;
  }
  public void setObjectData(byte[] objectData) {
    this.objectData = objectData;
  }
}
