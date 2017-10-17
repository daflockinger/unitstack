/*******************************************************************************
 * Copyright (C) 2017, Florian Mitterbauer
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/
package com.flockinger.unitstack.model.s3;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

/**
 * Represents the stored mock data of an Amazon S3 bucket including all <br>
 * the objects({@link S3Object}) stored in that bucket and the <br>
 * access-control-list data ({@link Grant}). <br>
 * @see {@link com.amazonaws.services.s3.model.Bucket}
 *
 */
@JacksonXmlRootElement(localName="Bucket")
public class Bucket {
  
  @JacksonXmlProperty(localName="Name")
  private String name;
 
  @JacksonXmlProperty(localName="CreationDate")
  private Date created;
  
  private List<S3Object> objects = new ArrayList<>();
  
  private List<Grant> accessControllList = new ArrayList<>();
  
  
  public List<Grant> getAccessControllList() {
    return accessControllList;
  }
  public void setAccessControllList(List<Grant> accessControllList) {
    this.accessControllList = accessControllList;
  }
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public Date getCreated() {
    return created;
  }
  public void setCreated(Date created) {
    this.created = created;
  }
  public List<S3Object> getObjects() {
    return objects;
  }
  public void setObjects(List<S3Object> objects) {
    this.objects = objects;
  }
}
