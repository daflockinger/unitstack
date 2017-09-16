package com.flockinger.unitstack;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;

import com.amazonaws.services.sns.model.AmazonSNSException;
import com.flockinger.unitstack.model.SnsMockParameters;
import com.flockinger.unitstack.model.sns.Topic;
import com.flockinger.unitstack.sns.SnsRequestTransformer;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

public abstract class UnitStackTest {
  
  public final static String UNIT_STACK_URL = "http://localhost";
  public final static int SNS_PORT = 4575;
  public final static String MOCK_PARAMS="MOCK_PARAMS";
  
  private Map<String, Topic> snsTopics = new HashMap<>();
  
  @Rule
  public WireMockRule snsMockRule = new WireMockRule(WireMockConfiguration.options()
      .port(SNS_PORT)
      .extensions(new SnsRequestTransformer(snsTopics)));
  
  protected <T extends AmazonSNSException> void mockSns(SnsMockParameters mockParameters) {
    snsMockRule.stubFor(post("/").willReturn(aResponse()
        .withTransformerParameter(MOCK_PARAMS, mockParameters)));
  }

  protected Map<String, Topic> getSnsTopics() {
    return snsTopics;
  }
}
