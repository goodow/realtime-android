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
package com.goodow.realtime.android;

import com.goodow.realtime.core.Handler;
import com.goodow.realtime.java.JavaScheduler;

import android.os.Looper;

class AndroidScheduler extends JavaScheduler {
  private final android.os.Handler handler;

  AndroidScheduler() {
    handler = new android.os.Handler(Looper.getMainLooper());
  }

  @Override
  public void scheduleDeferred(final Handler<Void> handler) {
    this.handler.post(new Runnable() {
      @Override
      public void run() {
        handler.handle(null);
      }
    });
  }
}