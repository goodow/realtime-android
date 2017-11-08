package com.goodow.realtime.android.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.goodow.realtime.android.mvp.Router;
import com.goodow.realtime.channel.AsyncResult;
import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.Handler;
import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.impl.SimpleBus;
import com.goodow.realtime.channel.util.AsyncResultHandler;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

  private Bus bus;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(com.goodow.realtime.android.demo.R.layout.activity_main);

    Router.init(getApplication());
    bus = new SimpleBus();

    bus.subscribe("views/#", new Handler<Message>() {
      @Override
      public void handle(Message message) {
        String url;
        if (message.topic().equals("views")) {
          boolean noData = message.payload() instanceof String;
          if (noData) {
            Router.getInstance().goToUrl((String) message.payload());
            return;
          }
          url = (String) ((Map) message.payload()).get("url");
        } else {
          url = "test://" + message.topic();
        }
        Router.getInstance().withData(message.payload()).goToUrl(url);
      }
    });

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
