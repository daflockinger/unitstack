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

@JacksonXmlRootElement(localName="ListMultipartUploadsResult",namespace="http://s3.amazonaws.com/doc/2006-03-01/")
public class ListMultipartResult {
    
    @JacksonXmlProperty(localName="Bucket")
    private String bucket;
    @JacksonXmlProperty(localName="Prefix")
    private String prefix;
    @JacksonXmlProperty(localName="KeyMarker")
    private String keyMarker;
    @JacksonXmlProperty(localName="UploadIdMarker")
    private String uploadIdMarker;
    @JacksonXmlProperty(localName="MaxUploads")
    private Integer maxUploads;
    @JacksonXmlProperty(localName="IsTruncated")
    private Boolean isTruncated = false;
    
    @JacksonXmlElementWrapper(useWrapping=false)
    @JacksonXmlProperty(localName="Upload")
    private List<PartUploadResult> uploads = new ArrayList<>();
    
    public ListMultipartResult() {}
    
    public ListMultipartResult(String bucket, MockRequest request) {
      String actionWithoutUpload = request.getBodyParameters().get(S3RequestTransformer.ACTION).replaceAll("uploads&", "");
      Map<String,String> listParameters = request.utils().queryStringToMap(actionWithoutUpload);
      this.bucket = bucket;
      this.prefix = listParameters.get("prefix");
      this.maxUploads = NumberUtils.toInt(listParameters.get("max-uploads"), ListObjectsResponder.MAX_KEYS);
      this.keyMarker = listParameters.get("key-marker");
    }
    
    public String getPrefix() {
      return prefix;
    }
    public void setPrefix(String prefix) {
      this.prefix = prefix;
    }
    public Boolean getIsTruncated() {
      return isTruncated;
    }
    public void setIsTruncated(Boolean isTruncated) {
      this.isTruncated = isTruncated;
    }
    public String getBucket() {
      return bucket;
    }
    public void setBucket(String bucket) {
      this.bucket = bucket;
    }
    public String getKeyMarker() {
      return keyMarker;
    }
    public void setKeyMarker(String keyMarker) {
      this.keyMarker = keyMarker;
    }
    public String getUploadIdMarker() {
      return uploadIdMarker;
    }
    public void setUploadIdMarker(String uploadIdMarker) {
      this.uploadIdMarker = uploadIdMarker;
    }
    public Integer getMaxUploads() {
      return maxUploads;
    }
    public void setMaxUploads(Integer maxUploads) {
      this.maxUploads = maxUploads;
    }
    public List<PartUploadResult> getUploads() {
      return uploads;
    }
    public void setUploads(List<PartUploadResult> uploads) {
      this.uploads = uploads;
    }
  }
