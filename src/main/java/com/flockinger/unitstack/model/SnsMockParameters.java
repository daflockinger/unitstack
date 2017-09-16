package com.flockinger.unitstack.model;

import com.amazonaws.services.sns.model.AmazonSNSException;

public class SnsMockParameters extends MockParameters{
  private Class<?> snsException;
  
  public SnsMockParameters() {
    setRequestSuccessfull(true);
  }
  
  public SnsMockParameters(String responseContent) {
    super();
    setResponseContent(responseContent);
  }
  
  public SnsMockParameters(Class<?> snsException) {
    setRequestSuccessfull(false);
    setSnsException(snsException);
  }
  
  public SnsMockParameters(Class<?> snsException, String errorMessage) {
    this(snsException);
    setErrorMessage(errorMessage);
  }

  public Class<?> getSnsException() {
    return snsException;
  }

  public void setSnsException(Class<?> snsException) {
    this.snsException = snsException;
  }
}
