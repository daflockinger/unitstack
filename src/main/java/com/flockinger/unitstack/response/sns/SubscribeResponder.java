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
package com.flockinger.unitstack.response.sns;

import java.util.UUID;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;

public class SubscribeResponder extends SnsResponder {

  private final static String SUBSCRIBE_ACTION = "Subscribe";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(SUBSCRIBE_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicArn = request.getBodyParameters().get("TopicArn");
    String protocol = request.getBodyParameters().get("Protocol");
    String endpoint = request.getBodyParameters().get("Endpoint");
    String subscriptionArn = topicArn + ":" + UUID.randomUUID().toString().substring(0, 5);

    if (request.getTopics().containsKey(topicArn)) {
      Subscription subscription = new Subscription();
      subscription.setEndpoint(endpoint);
      subscription.setProtocol(protocol);
      subscription.setSubscriptionArn(subscriptionArn);
      request.getTopics().get(topicArn).getSubscriptions().add(subscription);
    }
    return new MockResponse(request.utils().successBody(SUBSCRIBE_ACTION,
        "<SubscriptionArn>" + subscriptionArn + "</SubscriptionArn>"));
  }
}
