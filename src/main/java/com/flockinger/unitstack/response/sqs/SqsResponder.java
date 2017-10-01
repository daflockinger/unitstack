package com.flockinger.unitstack.response.sqs;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sqs.BatchEntry;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.transformer.SqsRequestTransformer;

import wiremock.org.apache.commons.lang3.StringUtils;

abstract class SqsResponder implements Responder {
  public abstract boolean isSameAction(MockRequest request);
  public abstract MockResponse createResponse(MockRequest request);
  
  private final static String KEY_PART_SEPARATOR = ".";
  private final ObjectMapper mapper = new ObjectMapper();
  private String batchRequestEntryPrefix;
  
  protected String extractQueueName(MockRequest request) {
    String urlPath = request.getBodyParameters().get(SqsRequestTransformer.PARAMETER_URL_NAME);
    return StringUtils.substringAfterLast(urlPath, "/");
  }
  
  protected List<BatchEntry> extractBatchEntries(MockRequest request, String prefix) {
    batchRequestEntryPrefix = prefix;
    return request.getBodyParameters().entrySet().stream()
        .filter(params -> params.getKey().startsWith(batchRequestEntryPrefix))
        .collect(toMap(this::keyWithoutPrefix,Entry::getValue))
    .entrySet().stream()
        .collect(groupingBy(this::sequenceNumber,toList()))
    .entrySet().stream()
        .map(entries -> entries.getValue().stream()
            .collect(toMap(this::extractPropertyName, Entry::getValue)))
        .map(this::mapToBatchEntry)
        .collect(toList());
  }
  
  private String keyWithoutPrefix(Entry<String,String> entry) {
    return entry.getKey().replace(batchRequestEntryPrefix, "");
  }
  
  private String sequenceNumber(Entry<String,String> entry) {
    return StringUtils.substringBefore(entry.getKey(), KEY_PART_SEPARATOR);
  }
  
  private String extractPropertyName(Entry<String, ?> entry) {
    return StringUtils.substringAfter(entry.getKey(), KEY_PART_SEPARATOR);
  }
  
  private BatchEntry mapToBatchEntry(Map<String,String> groupedEntries) {
    return mapper.convertValue(groupedEntries, BatchEntry.class);
  }
}
