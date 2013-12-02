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
package com.goodow.realtime.android;

import com.goodow.realtime.core.Net;
import com.goodow.realtime.core.Platform;
import com.goodow.realtime.core.VoidHandler;
import com.goodow.realtime.java.JavaPlatform;

import android.os.Looper;

public class AndroidPlatform extends JavaPlatform {
  public static AndroidPlatform register() {
    AndroidPlatform platform = new AndroidPlatform();
    Platform.setPlatform(platform);
    return platform;
  }

  private AndroidPlatform() {
  }

  @Override
  public Net net() {
    return net == null ? new AndroidNet(this) : net;
  }

  /**
   * A deferred command is executed after the event loop returns.
   */
  @Override
  public void scheduleDeferred(final VoidHandler handler) {
    android.os.Handler _handler = new android.os.Handler(Looper.getMainLooper());
    _handler.post(new Runnable() {
      @Override
      public void run() {
        handler.handle(null);
      }
    });
  }

  @Override
  public Type type() {
    return Type.ANDROID;
  }
}
