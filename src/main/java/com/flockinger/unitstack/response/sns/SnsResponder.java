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
package com.flockinger.unitstack.response.sns;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Subscription;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.response.Responder;

abstract class SnsResponder implements Responder {
  
  public abstract boolean isSameAction(MockRequest request);
  public abstract MockResponse createResponse(MockRequest request);

  
  protected String getAttributeResponseXml(Map<String,String> attributes) {
    String attributesXml = "";
    
    for(String attributeName : attributes.keySet()) {
      attributesXml += "<entry>" + "<key>" + attributeName + "</key>"  + 
    "<value>" + attributes.get(attributeName) + "</value>" + "</entry>";
    }
    return "<Attributes>" + attributesXml + "</Attributes>";
  }
  
  protected Optional<Subscription> findSubscriptionWithArn(String subscriptionArn, Collection<Topic> topics) {
    Optional<Topic> topic = findTopicWithSubscription(subscriptionArn,topics);
    Optional<Subscription> subscription = Optional.empty();
    if(topic.isPresent()) {
      subscription = topic.get().getSubscriptions().stream()
          .filter(sub -> hasSubscriptionArn(subscriptionArn,sub)).findFirst();
    }
    return subscription;
  }
  
  protected Optional<Topic> findTopicWithSubscription(String subscriptionArn, Collection<Topic> topics) {
    return topics.stream().filter(topic -> 
      topic.getSubscriptions().stream().anyMatch(subscription -> 
         hasSubscriptionArn(subscriptionArn,subscription))
    ).findAny();
  }
  
  protected boolean hasSubscriptionArn(String arn, Subscription subscription) {
    return StringUtils.equals(arn, subscription.getSubscriptionArn());
  }
}
