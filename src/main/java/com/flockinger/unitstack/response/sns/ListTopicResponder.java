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

import java.util.List;
import java.util.stream.Collectors;

import com.flockinger.unitstack.model.MockRequest;
import com.flockinger.unitstack.model.MockResponse;
import com.flockinger.unitstack.model.sns.Topic;

public class ListTopicResponder extends SnsResponder {

  private final static String LIST_TOPIC_ACTION = "ListTopics";

  @Override
  public boolean isSameAction(MockRequest request) {
    return request.utils().hasRequestAction(LIST_TOPIC_ACTION, request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicsXml = createTopicsXMLFrom(
        request.getTopics().values().stream().map(Topic::getTopicArn).collect(Collectors.toList()));

    return new MockResponse(request.utils().successBody(LIST_TOPIC_ACTION, topicsXml));
  }

  private String createTopicsXMLFrom(List<String> topicArns) {
    String topicsXml = "";

    for (String topicArn : topicArns) {
      topicsXml += "<member><TopicArn>" + topicArn + "</TopicArn></member>";
    }
    return "<Topics>" + topicsXml + "</Topics>";
  }

}
