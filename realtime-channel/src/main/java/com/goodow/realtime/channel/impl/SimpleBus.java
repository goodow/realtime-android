/*
 * Copyright 2013 Goodow.com
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.goodow.realtime.channel.impl;

import android.os.Looper;
import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.mqtt.Topic;
import com.goodow.realtime.channel.util.IdGenerator;
import com.goodow.realtime.channel.AsyncResult;
import com.goodow.realtime.channel.Handler;
import com.goodow.realtime.channel.Registration;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SimpleBus implements Bus {
  private static final Logger log = Logger.getLogger(SimpleBus.class.getName());

  static void checkNotNull(String paramName, Object param) {
    if (param == null) {
      throw new IllegalArgumentException("Parameter " + paramName + " must be specified");
    }
  }

  private LinkedHashMap<String, LinkedHashSet<Handler<Message>>> handlerMap;
  final LinkedHashMap<String, Handler<AsyncResult<Message>>> replyHandlers;
  final IdGenerator idGenerator;
  private final android.os.Handler handler;

  public SimpleBus() {
    handlerMap = new LinkedHashMap();
    replyHandlers = new LinkedHashMap();
    idGenerator = new IdGenerator();
    handler = new android.os.Handler(Looper.getMainLooper());
  }

  @Override
  public Bus publish(String topic, Object msg) {
    doSendOrPub(false, false, topic, msg, null);
    return this;
  }

  @Override
  public Bus publishLocal(String topic, Object msg) {
    doSendOrPub(true, false, topic, msg, null);
    return this;
  }

  @Override
  public Registration subscribe(final String topicFilter,
                                final Handler<? extends Message> handler) {
    return subscribeImpl(false, topicFilter, handler);
  }

  @Override
  public Registration subscribeLocal(final String topicFilter,
                                     final Handler<? extends Message> handler) {
    return subscribeImpl(true, topicFilter, handler);
  }

  @Override
  public <T> Bus send(String topic, Object msg, Handler<AsyncResult<Message<T>>> replyHandler) {
    doSendOrPub(false, true, topic, msg, replyHandler);
    return this;
  }

  @Override
  public <T> Bus sendLocal(String topic, Object msg, Handler<AsyncResult<Message<T>>> replyHandler) {
    doSendOrPub(true, true, topic, msg, replyHandler);
    return this;
  }

  protected boolean doSubscribe(boolean local, String topic,
                                Handler<? extends Message> handler) {
    checkNotNull("topic", topic);
    checkNotNull("handler", handler);
    LinkedHashSet handlers = handlerMap.get(topic);
    if (handlers == null) {
      handlers = new LinkedHashSet<>();
      handlers.add(handler);
      handlerMap.put(topic, handlers);
      return true;
    }
    if (!handlers.contains(handler)) {
      handlers.add(handler);
      return true;
    }
    return false;
  }

  @SuppressWarnings("unchecked")
  protected <T> void doSendOrPub(boolean local, boolean send, String topic, Object msg,
                                 final Handler<AsyncResult<Message<T>>> replyHandler) {
    checkNotNull("topic", topic);
    String replyTopic = null;
    if (replyHandler != null) {
      replyTopic = makeUUID();
      replyHandlers.put(replyTopic, (Handler) replyHandler);
    }
    MessageImpl message = new MessageImpl(local, send, this, topic, replyTopic, msg);
    doReceiveMessage(message);
  }

  protected boolean doUnsubscribe(boolean local, String topic,
                                  Handler<? extends Message> handler) {
    checkNotNull("topic", topic);
    checkNotNull("handler", handler);
    LinkedHashSet handlers = handlerMap.get(topic);
    if (handlers == null) {
      return false;
    }
    boolean removed = handlers.remove(handler);
    if (handlers.isEmpty()) {
      handlerMap.remove(topic);
    }
    return removed;
  }

  String makeUUID() {
    return idGenerator.next(36);
  }

  private void doReceiveMessage(final Message message) {
    final String topic = message.topic();
    // Might be a reply message
    final Handler<AsyncResult<Message>> handler = replyHandlers.get(topic);
    if (handler != null) {
      replyHandlers.remove(topic);
      scheduleHandle(topic, new Handler<Message>() {
        @Override
        public void handle(Message event) {
          handler.handle(new AsyncResultImpl(message));
        }
      }, message);
      return;
    }

    Topic topic1 = new Topic(topic);
    for (String topicFilter : handlerMap.keySet()) {
      if (!topic1.match(new Topic(topicFilter))) {
        continue;
      }
      LinkedHashSet handlers = handlerMap.get(topicFilter);
      if (handlers != null) {
        // We make a copy since the handler might get unregistered from within the handler itself,
        // which would screw up our iteration
        LinkedHashSet<Handler<Message>> copy = (LinkedHashSet) handlers.clone();
        for (Handler<Message> value : copy) {
          scheduleHandle(topicFilter, value, message);
        }
      }
    }
  }


  private void scheduleHandle(final String topic, final Handler<Message> handler, final Message message) {
    try {
      this.handler.post(new Runnable() {
        @Override
        public void run() {
          handler.handle(message);
        }
      });
    } catch (Throwable e) {
      log.log(Level.WARNING, "Failed to handle on topic: " + topic, e);
      Map<String, Object> msg = new HashMap<>();
      msg.put("topic", topic);
      msg.put("message", message);
      msg.put("cause", e);
//      publishLocal(ON_ERROR, msg);
    }
  }

  private Registration subscribeImpl(final boolean local, final String topic,
                                     final Handler<? extends Message> handler) {
    doSubscribe(local, topic, handler);
    return new Registration() {
      @Override
      public void unregister() {
        doUnsubscribe(local, topic, handler);
      }
    };
  }
}