/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.model;

import com.amazonaws.regions.Regions;

/**
 * Parameters for setting up the mock.
 *
 */
public class MockParameters {
  private boolean isRequestSuccessfull = true;
  private String errorMessage;
  private Class<?> snsException;
  private String mockRegion;

  public MockParameters() {
    setRequestSuccessfull(true);
  }

  /**
   * Sets exception for the error response of the next request.
   * 
   * @param snsException The exception to be responded by the mock
   */
  public MockParameters(Class<?> snsException) {
    setRequestSuccessfull(false);
    setSnsException(snsException);
  }

  /**
   * Sets exception and error message for the error response of the next request.
   * 
   * @param snsException The exception to be responded by the mock
   * @param errorMessage Error message in the failed response
   */
  public MockParameters(Class<?> snsException, String errorMessage) {
    this(snsException);
    setErrorMessage(errorMessage);
  }

  public boolean isRequestSuccessfull() {
    return isRequestSuccessfull;
  }

  /**
   * Determines if the response should be a successfull one or an error response.
   * 
   * @param isRequestSuccessfull Should the response be successful
   */
  public void setRequestSuccessfull(boolean isRequestSuccessfull) {
    this.isRequestSuccessfull = isRequestSuccessfull;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  /**
   * Sets the error message for the error response.
   * 
   * @param errorMessage Error message in the failed response
   */
  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public Class<?> getSnsException() {
    return snsException;
  }

  /**
   * Sets the exception for the error response.
   * 
   * @param snsException The exception to be responded by the mock
   */
  public void setSnsException(Class<?> snsException) {
    this.snsException = snsException;
  }

  public String getMockRegion() {
    return mockRegion;
  }

  /**
   * Sets the data-center region for the mock. Please use real AWS regions ({@link Regions}), as
   * this is checked sometimes.
   * 
   * @param mockRegion Valid AWS Region name in text form
   */
  public void setMockRegion(String mockRegion) {
    this.mockRegion = mockRegion;
  }
}
