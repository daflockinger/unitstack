package com.flockinger.unitstack.model.sqs;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import wiremock.org.eclipse.jetty.util.ArrayQueue;

public class AwsQueue {
  private String name;
  private String url;
  private Queue<SqsMessage> messageQueue = new ArrayQueue<>();
  private Map<String,Queue<SqsMessage>> invisibilityQueues = new HashMap<>();

  public AwsQueue(String name, String url) {
    super();
    this.name = name;
    this.url = url;
  }
  
  public String getName() {
    return name;
  }
  public void setName(String name) {
    this.name = name;
  }
  public String getUrl() {
    return url;
  }
  public void setUrl(String url) {
    this.url = url;
  }
  public Queue<SqsMessage> getMessageQueue() {
    return messageQueue;
  }

  public Queue<SqsMessage> getInvisibilityQueueFor(String receiptHandle) {
    if(!invisibilityQueues.containsKey(receiptHandle)) {
      invisibilityQueues.put(receiptHandle, new ArrayQueue<>());
    }
    return invisibilityQueues.get(receiptHandle);
  }
  
  public void purge() {
    messageQueue.clear();
    invisibilityQueues.values().forEach(queue -> queue.clear());
  }
}
