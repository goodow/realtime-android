package com.goodow.realtime.java;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.MessageHandler;
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.core.Handler;

import org.junit.Assert;

import java.io.IOException;

public class LocalEventBusDemo {
  static class Any {
    String str;
    LocalEventBusDemo demo;

    public Any(String str, LocalEventBusDemo demo) {
      this.str = str;
      this.demo = demo;
    }
  }

  static {
    JavaPlatform.register();
  }

  public static void main(String[] args) throws IOException {
    Bus bus = new SimpleBus();
    final LocalEventBusDemo demo = new LocalEventBusDemo();

    bus.registerHandler("someaddress", new MessageHandler<Any>() {
      @Override
      public void handle(Message<Any> message) {
        Assert.assertEquals("some string", message.body().str);
        Assert.assertSame(demo, message.body().demo);

        message.reply("reply");
      }
    });

    bus.send("someaddress", new Any("some string", demo), new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        Assert.assertEquals("reply", message.body());

        System.exit(0);
      }
    });

    // Prevent the JVM from exiting
    System.in.read();
  }
}
