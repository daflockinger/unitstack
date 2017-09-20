package com.flockinger.unitstack.model;

public class MockParameters {
  private boolean isRequestSuccessfull = true;
  private String responseContent;
  private String errorMessage;
  private Class<?> snsException;
  
  public MockParameters() {
    setRequestSuccessfull(true);
  }
  public MockParameters(String responseContent) {
    super();
    setResponseContent(responseContent);
  }
  public MockParameters(Class<?> snsException) {
    setRequestSuccessfull(false);
    setSnsException(snsException);
  }
  public MockParameters(Class<?> snsException, String errorMessage) {
    this(snsException);
    setErrorMessage(errorMessage);
  }
  
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
  public Class<?> getSnsException() {
    return snsException;
  }
  public void setSnsException(Class<?> snsException) {
    this.snsException = snsException;
  }
}
