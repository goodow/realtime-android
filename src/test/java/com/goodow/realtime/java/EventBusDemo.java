/*
 * Copyright 2014 Goodow.com
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
package com.goodow.realtime.java;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.MessageHandler;
import com.goodow.realtime.channel.impl.ReconnectBus;
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.channel.util.IdGenerator;
import com.goodow.realtime.core.Handler;
import com.goodow.realtime.core.HandlerRegistration;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonObject;

import org.junit.Assert;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EventBusDemo {
  private static final String ADDR = "java.someaddress." + new IdGenerator().next(5);
  private static final Logger log = Logger.getLogger(EventBusDemo.class.getName());
  private static HandlerRegistration handlerRegs;
  static {
    JavaPlatform.register();
  }

  public static void main(String[] args) throws IOException {
    final Bus bus =
        new ReconnectBus("ws://data.goodow.com:8080/eventbus/websocket", Json.createObject()
            .set(SimpleBus.MODE_MIX, true));
    final HandlerRegistration openHandlerReg =
        bus.registerHandler(Bus.LOCAL_ON_OPEN, new MessageHandler<JsonObject>() {
          @Override
          public void handle(Message<JsonObject> message) {
            handlerEventBusOpened(bus);
          }
        });
    final HandlerRegistration closeHandlerReg =
        bus.registerHandler(Bus.LOCAL_ON_CLOSE, new MessageHandler<JsonObject>() {
          @Override
          public void handle(Message<JsonObject> message) {
            log.info("EventBus closed");
            handlerRegs.unregisterHandler();
            handlerRegs = null;

            System.exit(0);
          }
        });
    final HandlerRegistration errorHandlerReg =
        bus.registerHandler(Bus.LOCAL_ON_ERROR, new MessageHandler<JsonObject>() {
          @Override
          public void handle(Message<JsonObject> message) {
            log.log(Level.SEVERE, "EventBus Error");
          }
        });

    final HandlerRegistration handlerRegistration =
        bus.registerHandler(ADDR, new MessageHandler<JsonObject>() {
          @Override
          public void handle(Message<JsonObject> message) {
            Assert.assertEquals("send1", message.body().getString("text"));

            JsonObject o1 = Json.createObject().set("text", "reply1");
            message.reply(o1, new Handler<Message<JsonObject>>() {
              @Override
              public void handle(Message<JsonObject> message) {
                Assert.assertEquals("reply2", message.body().getString("text"));
                Assert.assertNull(message.replyAddress());

                bus.close();
              }
            });
          }
        });

    handlerRegs = new HandlerRegistration() {
      @Override
      public void unregisterHandler() {
        openHandlerReg.unregisterHandler();
        closeHandlerReg.unregisterHandler();
        errorHandlerReg.unregisterHandler();
        handlerRegistration.unregisterHandler();
      }
    };

    // Prevent the JVM from exiting
    System.in.read();
  }

  private static void handlerEventBusOpened(final Bus bus) {
    JsonObject o1 = Json.createObject().set("text", "send1");
    bus.send(ADDR, o1, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        Assert.assertEquals("reply1", message.body().getString("text"));

        JsonObject o1 = Json.createObject().set("text", "reply2");
        message.reply(o1);
      }
    });
  }
}
