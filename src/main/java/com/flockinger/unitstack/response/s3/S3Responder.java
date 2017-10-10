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

import static org.apache.commons.lang3.StringUtils.isNotEmpty;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.UnitStackTest;
import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.s3.Bucket;
import com.flockinger.unitstack.model.s3.S3Object;
import com.flockinger.unitstack.response.Responder;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

abstract class S3Responder implements Responder {
  public abstract boolean isSameAction(MockRequest request);

  public abstract MockResponse createResponse(MockRequest request);

  private final static Pattern bucketFromUrlPattern =
      Pattern.compile("^(?:http|https)://([^\\.[^l]]*)."
          + StringUtils.substringAfterLast(UnitStackTest.UNIT_STACK_URL, "/") + ":"
          + UnitStackTest.S3_PORT + "(?:.*)$");


  protected String getBucketFromUrl(MockRequest request) {
    String url = request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME);
    String bucketName = null;
    Matcher matcher = bucketFromUrlPattern.matcher(url);

    if (matcher.find()) {
      bucketName = matcher.group(1);
    }
    return bucketName;
  }

  protected String successBody(String action, String content) {
    return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<" + action
        + "Result xmlns=\"http://s3.amazonaws.com/doc/2006-03-01\">\n" + content + "</" + action
        + "Result>";
  }

  protected Optional<String> getObjectKey(MockRequest request) {
    String key = null;

    URI url;
    try {
      url = new URI(request.getBodyParameters().get(S3RequestTransformer.PARAMETER_URL_NAME));
      key = url.getPath().replaceFirst("/", "");
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return StringUtils.isEmpty(key) ? Optional.empty() : Optional.ofNullable(key);
  }

  protected Optional<Bucket> getBucketFromRequest(MockRequest request) {
    String name = getBucketFromUrl(request);
    Optional<Bucket> bucket = Optional.empty();
    if (isNotEmpty(name) && request.getBuckets().containsKey(name)) {
      bucket = Optional.of(request.getBuckets().get(name));
    }
    return bucket;
  }
  
  protected Optional<S3Object> getObject(Bucket bucket, String key) {
    return bucket.getObjects().stream().filter(object -> StringUtils.equals(object.getKey(),key)).findFirst();
  }
}
