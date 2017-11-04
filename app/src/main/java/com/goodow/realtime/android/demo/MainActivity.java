package com.goodow.realtime.android.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.channel.AsyncResult;
import com.goodow.realtime.channel.util.AsyncResultHandler;
import com.goodow.realtime.channel.Handler;

public class MainActivity extends AppCompatActivity {

  private Bus bus;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.goodow.realtime.android.demo.R.layout.activity_main);

    bus = new SimpleBus();
    bus.subscribe("#", new Handler<Message>() {
      @Override
      public void handle(Message message) {
        System.out.print(message.payload());
        message.reply("Pong!", new AsyncResultHandler<String>() {
          @Override
          public void handle(AsyncResult<Message<String>> asyncResult) {
            if (asyncResult.failed()) {
              return;
            }
            Message<String> message = asyncResult.result();
            System.out.println(message.payload());
          }
        });
      }
    });

    bus.send("testTopic/abc", "Ping!", new AsyncResultHandler<String>() {

      @Override
      public void handle(AsyncResult<Message<String>> asyncResult) {
        if (asyncResult.failed()) {
          return;
        }
        Message<String> message = asyncResult.result();
        System.out.println(message.payload());
        message.reply("Ping 2");
      }
    });



  }
}
