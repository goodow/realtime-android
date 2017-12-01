package com.goodow.realtime.channel.util;

import com.goodow.realtime.channel.Bus;
import com.goodow.realtime.channel.firebase.FirebaseChannel;
import com.goodow.realtime.channel.impl.SimpleBus;

/**
 * Created by larry on 2017/12/1.
 */
public class BusProvider {
  private volatile static Bus instance = null;
  private static FirebaseChannel firebaseChannel;

  public static Bus get() {
    if (instance != null) {
      return instance;
    }
    synchronized (BusProvider.class) {
      if (instance == null) {
        instance = new SimpleBus();
      }
    }
    return instance;
  }

  public static Bus enableRemoteBus() {
    if (firebaseChannel != null) {
      return BusProvider.get();
    }
    firebaseChannel = new FirebaseChannel(BusProvider.get());
    return BusProvider.get();
  }
}