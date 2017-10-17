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

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.response.DefaultResponder;
import com.flockinger.unitstack.transformer.S3RequestTransformer;

public class DefaultS3Responder extends DefaultResponder {
  @Override
  public MockResponse createResponse(MockRequest request) {
    String action = request.getBodyParameters().get(S3RequestTransformer.ACTION);

    if (StringUtils.equals(action, "requestPayment")) {
      return new MockResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<RequestPaymentConfiguration xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n"
          + "  <Payer>Requester</Payer></RequestPaymentConfiguration>");
    }
    if (StringUtils.endsWith(action, "tagging")) {
      return new MockResponse("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
          + "<Tagging xmlns=\"http://s3.amazonaws.com/doc/2006-03-01/\">\n"
          + "<TagSet></TagSet></Tagging> ");
    }
    if (StringUtils.isEmpty(action)) {
      return new MockResponse("");
    }
    return super.createResponse(request);
  }
}
