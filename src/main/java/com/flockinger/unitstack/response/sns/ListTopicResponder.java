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
    return request.utils().hasRequestAction(LIST_TOPIC_ACTION,request);
  }

  @Override
  public MockResponse createResponse(MockRequest request) {
    String topicsXml = createTopicsXMLFrom(request.getTopics().values()
        .stream().map(Topic::getTopicArn).collect(Collectors.toList()));
    
    return new MockResponse(request.utils().successBody(LIST_TOPIC_ACTION, topicsXml));
  }
  
  private String createTopicsXMLFrom(List<String> topicArns) {
    String topicsXml = "";
    
    for(String topicArn : topicArns) {
      topicsXml += "<member><TopicArn>" + topicArn + "</TopicArn></member>";
    }
    return "<Topics>" + topicsXml + "</Topics>";
  }

}
