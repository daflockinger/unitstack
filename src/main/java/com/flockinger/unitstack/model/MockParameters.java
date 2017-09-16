package com.flockinger.unitstack.model;

public class MockParameters {
  private boolean isRequestSuccessfull;
  private String responseContent;
  private String errorMessage;
  
  public boolean isRequestSuccessfull() {
    return isRequestSuccessfull;
  }
  public void setRequestSuccessfull(boolean isRequestSuccessfull) {
    this.isRequestSuccessfull = isRequestSuccessfull;
  }
  public String getResponseContent() {
    return responseContent;
  }
  public void setResponseContent(String responseContent) {
    this.responseContent = responseContent;
  }
  public String getErrorMessage() {
    return errorMessage;
  }
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
