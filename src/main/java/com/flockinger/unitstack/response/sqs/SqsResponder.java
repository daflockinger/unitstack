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
        .collect(toMap(this::keyWithoutPrefix, Entry::getValue)).entrySet().stream()
        .collect(groupingBy(this::sequenceNumber, toList())).entrySet().stream()
        .map(entries -> entries.getValue().stream()
            .collect(toMap(this::extractPropertyName, Entry::getValue)))
        .map(this::mapToBatchEntry).collect(toList());
  }

  private String keyWithoutPrefix(Entry<String, String> entry) {
    return entry.getKey().replace(batchRequestEntryPrefix, "");
  }

  private String sequenceNumber(Entry<String, String> entry) {
    return StringUtils.substringBefore(entry.getKey(), KEY_PART_SEPARATOR);
  }

  private String extractPropertyName(Entry<String, ?> entry) {
    return StringUtils.substringAfter(entry.getKey(), KEY_PART_SEPARATOR);
  }

  private BatchEntry mapToBatchEntry(Map<String, String> groupedEntries) {
    return mapper.convertValue(groupedEntries, BatchEntry.class);
  }
}
