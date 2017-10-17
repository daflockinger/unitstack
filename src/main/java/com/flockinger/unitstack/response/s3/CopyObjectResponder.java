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
package com.flockinger.unitstack.response.s3;

import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Action;
import com.flockinger.unitstack.model.s3.S3Object;


public class CopyObjectResponder extends S3Responder {

  private final static String COPY_SOURCE_HEADER = "x-amz-copy-source";
  private final static Pattern SOURCE_EXTRACTION_PATTERN = Pattern.compile("/([^/]*)/(.*)");

  @Override
  public boolean isSameAction(MockRequest request) {
    return S3ActionInvestigator.get().isAction(request, S3Action.COPY_OBJECT);
  }

  @SuppressWarnings("deprecation")
  @Override
  public MockResponse createResponse(MockRequest request) {
    Optional<String> targetKey = getObjectKey(request);
    Optional<Bucket> targetBucket = getBucket(request);
    Optional<S3Object> source = getSourceFromHeader(request);

    if (request.utils().areAllPresent(targetKey, targetBucket, source)) {
      S3Object copy = SerializationUtils.clone(source.get());
      copy.setKey(targetKey.get());
      targetBucket.get().getObjects().add(copy);
    }

    return new MockResponse(
        successBody("CopyObject", "<LastModified>" + ISO8601Utils.format(new Date())
            + "</LastModified>\n" + "  <ETag>" + UUID.randomUUID().toString() + "</ETag>"));
  }

  private Optional<S3Object> getSourceFromHeader(MockRequest request) {
    Optional<S3Object> source = Optional.empty();
    Matcher sourceMatcher =
        SOURCE_EXTRACTION_PATTERN.matcher(request.getBodyParameters().get(COPY_SOURCE_HEADER));
    sourceMatcher.find();
    Optional<Bucket> bucket = request.getBuckets().values().stream()
        .filter(oneBucket -> isCorrectBucket(oneBucket, sourceMatcher)).findAny();

    if (bucket.isPresent()) {
      source = bucket.get().getObjects().stream()
          .filter(s3Object -> hasS3ObjectKey(s3Object, sourceMatcher)).findAny();
    }
    return source;
  }

  private boolean isCorrectBucket(Bucket bucket, Matcher bucketMatcher) {
    return bucketMatcher.groupCount() > 0
        && StringUtils.equals(bucket.getName(), bucketMatcher.group(1));
  }

  private boolean hasS3ObjectKey(S3Object s3Object, Matcher sourceMatcher) {
    return sourceMatcher.groupCount() > 1
        && StringUtils.equals(s3Object.getKey(), sourceMatcher.group(2));
  }
}
