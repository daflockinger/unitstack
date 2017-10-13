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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.math.NumberUtils;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.response.s3.ListObjectsResponder;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

@JacksonXmlRootElement(localName="ListBucketResult",namespace="http://s3.amazonaws.com/doc/2006-03-01/")
public class ListBucketResult {
  
  @JacksonXmlProperty(localName="Name")
  private String name;
  @JacksonXmlProperty(localName="Prefix")
  private String prefix;
  @JacksonXmlProperty(localName="Marker")
  private String marker;
  @JacksonXmlProperty(localName="MaxKeys")
  private Integer maxKeys;
  @JacksonXmlProperty(localName="IsTruncated")
  private Boolean isTruncated = false;
  
  @JacksonXmlElementWrapper(useWrapping=false)
  @JacksonXmlProperty(localName="Contents")
  private List<ObjectSummary> contents = new ArrayList<>();
  
  public ListBucketResult() {}
  
  public ListBucketResult(String name, MockRequest request) {
    Map<String,String> listParameters = request.utils().queryStringToMap(request.getBodyParameters().get(S3RequestTransformer.ACTION));
    this.name = name;
    this.prefix = listParameters.get("prefix");
    this.maxKeys = NumberUtils.toInt(listParameters.get("max-keys"), ListObjectsResponder.MAX_KEYS);
    this.marker = listParameters.get("marker");
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getPrefix() {
    return prefix;
  }
  public void setPrefix(String prefix) {
    this.prefix = prefix;
  }
  public String getMarker() {
    return marker;
  }
  public void setMarker(String marker) {
    this.marker = marker;
  }
  public Integer getMaxKeys() {
    return maxKeys;
  }
  public void setMaxKeys(Integer maxKeys) {
    this.maxKeys = maxKeys;
  }
  public Boolean getIsTruncated() {
    return isTruncated;
  }
  public void setIsTruncated(Boolean isTruncated) {
    this.isTruncated = isTruncated;
  }
  public List<ObjectSummary> getContents() {
    return contents;
  }
  public void setContents(List<ObjectSummary> contents) {
    this.contents = contents;
  }
}
