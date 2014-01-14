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
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.core.Handler;
import com.goodow.realtime.core.HandlerRegistration;

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

  private static HandlerRegistration handlerRegistration;

  static {
    JavaPlatform.register();
  }

  public static void main(String[] args) throws IOException {
    Bus bus = new SimpleBus();
    final LocalEventBusDemo demo = new LocalEventBusDemo();

    handlerRegistration = bus.registerHandler("someaddress", new MessageHandler<Any>() {
      @Override
      public void handle(Message<Any> message) {
        Assert.assertEquals("some string", message.body().str);
        Assert.assertSame(demo, message.body().demo);

        message.reply("reply");

        handlerRegistration.unregisterHandler();
        handlerRegistration = null;
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
