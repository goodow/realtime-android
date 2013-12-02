package com.goodow.realtime.java;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.MessageHandler;
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.core.Handler;
import com.goodow.realtime.json.Json;
import com.goodow.realtime.json.JsonObject;

import org.junit.Assert;

import java.io.IOException;

public class LocalEventBusDemo {
  public static void main(String[] args) throws IOException {
    Bus bus = new SimpleBus();

    bus.registerHandler("someaddress", new MessageHandler<JsonObject>() {
      @Override
      public void handle(Message<JsonObject> message) {
        Assert.assertEquals("send1", message.body().getString("text"));

        JsonObject o1 = Json.createObject().set("text", "reply1");
        message.reply(o1);
      }
    });

    JsonObject o1 = Json.createObject().set("text", "send1");
    bus.send("someaddress", o1, new Handler<Message<JsonObject>>() {
      @Override
      public void handle(Message<JsonObject> message) {
        Assert.assertEquals("reply1", message.body().getString("text"));

        System.exit(0);
      }
    });

    // Prevent the JVM from exiting
    System.in.read();
  }
}
