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
package com.flockinger.unitstack.model.s3;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


/**
 * Represents the mocked object and data stored in Amazon S3.
 * 
 * @see com.amazonaws.services.s3.model.S3Object
 *
 */
public class S3Object implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = -1505612701802871706L;

  private String key;
  private byte[] objectData;
  private String md5;
  private LinkedList<S3Part> parts = new LinkedList<>();

  private List<Grant> accessControllList;


  public LinkedList<S3Part> getParts() {
    return parts;
  }

  public void setParts(LinkedList<S3Part> parts) {
    this.parts = parts;
  }

  public String getMd5() {
    return md5;
  }

  public void setMd5(String md5) {
    this.md5 = md5;
  }

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
