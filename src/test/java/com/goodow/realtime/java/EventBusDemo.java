package com.goodow.realtime.java;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.MessageHandler;
import com.goodow.realtime.channel.impl.WebSocketBusClient;
import com.goodow.realtime.core.Handler;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonObject;

import org.junit.Assert;

import java.io.IOException;
import java.util.logging.Logger;

public class EventBusDemo {
  private static final Logger log = Logger.getLogger(EventBusDemo.class.getName());
  static {
    JavaPlatform.register();
  }

  public static void main(String[] args) throws IOException {
    final Bus bus =
        new WebSocketBusClient("ws://data.goodow.com:8080/eventbus/websocket", Json.createObject()
            .set("forkLocal", true));
    bus.registerHandler(Bus.LOCAL_ON_OPEN, new MessageHandler<JsonObject>() {
      @Override
      public void handle(Message<JsonObject> message) {
        handlerEventBusOpened(bus);
      }
    });
    bus.registerHandler(Bus.LOCAL_ON_CLOSE, new MessageHandler<JsonObject>() {
      @Override
      public void handle(Message<JsonObject> message) {
        log.info("EventBus closed");
        System.exit(0);
      }
    });

    bus.registerHandler("java.someaddress", new MessageHandler<JsonObject>() {
      @Override
      public void handle(Message<JsonObject> message) {
        Assert.assertEquals("send1", message.body().getString("text"));

        JsonObject o1 = Json.createObject();
        o1.set("text", "reply1");
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

    // Prevent the JVM from exiting
    System.in.read();
  }

  private static void handlerEventBusOpened(final Bus bus) {
    JsonObject o1 = Json.createObject();
    o1.set("text", "send1");
    bus.send("java.someaddress", o1, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        Assert.assertEquals("reply1", message.body().getString("text"));

        JsonObject o1 = Json.createObject();
        o1.set("text", "reply2");
        message.reply(o1);
      }
    });
  }
}
