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

import java.util.UUID;
import java.util.stream.Collectors;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Action;

public class ListBucketsResponder extends S3Responder {

  private final static String LIST_BUCKETS_ACTION = "ListAllMyBuckets";

  @Override
  public MockResponse createResponse(MockRequest request) {

    String bucketsXml = "";

    if (request.getBuckets().size() > 0) {
      bucketsXml = request.getBuckets().values().stream()
          .map(bucket -> getBucketXml(bucket, request)).collect(Collectors.joining("\n"));
    }
    String content = " <Owner>\n" + "    <ID>" + UUID.randomUUID().toString() + "</ID>\n"
        + "    <DisplayName>" + UUID.randomUUID().toString() + "</DisplayName>\n"
        + "  </Owner><Buckets>" + bucketsXml + "</Buckets>";

    return new MockResponse(successBody(LIST_BUCKETS_ACTION, content));
  }

  private String getBucketXml(Bucket bucket, MockRequest request) {
    return request.utils().toXmlString(bucket);
  }

  @Override
  public boolean isSameAction(MockRequest request) {
    return S3ActionInvestigator.get().isAction(request, S3Action.LIST_BUCKETS);
  }
}
