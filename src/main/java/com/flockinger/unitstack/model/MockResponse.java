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

/**
 * Represents all the data needed for the {@link ResponseDefinitionTransformer} <br>
 * implementation to create a proper response to the stub caller.
 *
 */
public class MockResponse {
  private int status = 200;
  private String body;
  private byte[] binaryBody;

  /**
   * Initialize successful response with binary body.
   * 
   * @param binaryBody
   */
  public MockResponse(byte[] binaryBody) {
    this.binaryBody = binaryBody;
  }

  /**
   * Initialize successful response with text body.
   * 
   * @param body
   */
  public MockResponse(String body) {
    this.body = body;
  }

  /**
   * Initialize response with binary body and response status.
   * 
   * @param status
   * @param body
   */
  public MockResponse(int status, String body) {
    this(body);
    this.status = status;
  }

  /**
   * The message body in binary form (file download).
   * 
   * @return
   */
  public byte[] getBinaryBody() {
    return binaryBody;
  }

  /**
   * The HTTP response status (usually 200).
   * 
   * @return
   */
  public int getStatus() {
    return status;
  }


  /**
   * Sets the HTTP response status.
   * 
   * @param status
   */
  public void setStatus(int status) {
    this.status = status;
  }

  /**
   * The message body in text form (JSON,XML,...).
   * 
   * @return
   */
  public String getBody() {
    return body;
  }

  /**
   * Sets the message body in text form (JSON,XML,...).
   * 
   * @param body
   */
  public void setBody(String body) {
    this.body = body;
  }
}
