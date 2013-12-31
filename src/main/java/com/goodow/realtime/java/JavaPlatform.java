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
package com.goodow.realtime.java;

import com.goodow.realtime.core.Net;
import com.goodow.realtime.core.Platform;
import com.goodow.realtime.core.Platform.Type;
import com.goodow.realtime.core.PlatformFactory;
import com.goodow.realtime.core.VoidHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

public class JavaPlatform implements PlatformFactory {
  /**
   * Registers the Java platform with a default configuration.
   */
  public static void register() {
    Platform.setFactory(new JavaPlatform());
  }

  private final AtomicInteger timerId;
  private final Map<Integer, TimerTask> timers;
  private final Timer timer;
  protected JavaNet net;

  protected JavaPlatform() {
    timerId = new AtomicInteger(1);
    timers = new HashMap<Integer, TimerTask>();
    timer = new Timer(true);
  }

  @Override
  public boolean cancelTimer(int id) {
    if (timers.containsKey(id)) {
      timers.get(id).cancel();
      timers.remove(id);
      return true;
    }
    return false;
  }

  @Override
  public Net net() {
    return net == null ? new JavaNet() : net;
  }

  @Override
  public void scheduleDeferred(final VoidHandler handler) {
    new Thread() {
      @Override
      public void run() {
        handler.handle(null);
      }
    }.start();
  }

  @Override
  public int setPeriodic(int delayMs, final VoidHandler handler) {
    final int id = timerId.getAndIncrement();
    TimerTask task = new TimerTask() {
      @Override
      public void run() {
        handler.handle(null);
      }
    };
    timers.put(id, task);
    timer.scheduleAtFixedRate(task, delayMs, delayMs);
    return id;
  }

  @Override
  public Type type() {
    return Type.JAVA;
  }
}