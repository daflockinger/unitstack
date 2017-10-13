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
package com.flockinger.unitstack.model.s3.dto;

import java.util.UUID;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

@JacksonXmlRootElement(localName="Deleted")
public class DeletedObject {

  @JacksonXmlProperty(localName="Key")
  private String key;
  
  @JacksonXmlProperty(localName="VersionId")
  private String versionId;
  
  @JacksonXmlProperty(localName="DeleteMarker")
  private String deleteMarker;
  
  @JacksonXmlProperty(localName="DeleteMarkerVersionId")
  private String deleteMarkerVersionId;

  public DeletedObject(String key) {
    this.key = key;
    this.versionId = UUID.randomUUID().toString();
    this.deleteMarkerVersionId = UUID.randomUUID().toString();
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getVersionId() {
    return versionId;
  }

  public void setVersionId(String versionId) {
    this.versionId = versionId;
  }

  public String getDeleteMarker() {
    return deleteMarker;
  }

  public void setDeleteMarker(String deleteMarker) {
    this.deleteMarker = deleteMarker;
  }

  public String getDeleteMarkerVersionId() {
    return deleteMarkerVersionId;
  }

  public void setDeleteMarkerVersionId(String deleteMarkerVersionId) {
    this.deleteMarkerVersionId = deleteMarkerVersionId;
  }
}
