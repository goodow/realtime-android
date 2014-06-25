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
import com.goodow.realtime.core.Registration;

import org.junit.Test;
import org.vertx.testtools.TestVerticle;
import org.vertx.testtools.VertxAssert;

public class SimpleBusTest extends TestVerticle {
  static class Any {
    String str;
    SimpleBusTest demo;

    public Any(String str, SimpleBusTest demo) {
      this.str = str;
      this.demo = demo;
    }
  }

  private final Bus bus = new SimpleBus();
  private Registration reg;
  static {
    JavaPlatform.register();
  }

  @Test
  public void testLocal() {
    final SimpleBusTest demo = new SimpleBusTest();

    reg = bus.subscribeLocal("some/topic", new MessageHandler<Any>() {
      @Override
      public void handle(Message<Any> message) {
        VertxAssert.assertEquals("some string", message.body().str);
        VertxAssert.assertSame(demo, message.body().demo);

        message.reply("reply", null);

        reg.unregister();
        reg = null;
      }
    });

    bus.sendLocal("some/topic", new Any("some string", demo), new Handler<Message<String>>() {
      @Override
      public void handle(Message<String> message) {
        VertxAssert.assertEquals("reply", message.body());

        VertxAssert.testComplete();
      }
    });
  }
}
