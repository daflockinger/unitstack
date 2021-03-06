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
package com.flockinger.unitstack.model.sqs;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

import wiremock.org.eclipse.jetty.util.ArrayQueue;

/**
 * Represents the stored mock data of an SQS Queue and all <br>
 * the data inside of it like the messages({@link SqsMessage}).
 */
public class AwsQueue {
  private String name;
  private String url;
  private Queue<SqsMessage> messageQueue = new ArrayQueue<>();
  private Map<String, Queue<SqsMessage>> invisibilityQueues = new HashMap<>();

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
    if (!invisibilityQueues.containsKey(receiptHandle)) {
      invisibilityQueues.put(receiptHandle, new ArrayQueue<>());
    }
    return invisibilityQueues.get(receiptHandle);
  }

  public void purge() {
    messageQueue.clear();
    invisibilityQueues.values().forEach(queue -> queue.clear());
  }
}
