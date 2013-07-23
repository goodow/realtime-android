/*
 * Copyright 2012 Goodow.com
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
package com.goodow.realtime.extensions.android;

import com.goodow.realtime.channel.http.HttpTransport;
import com.goodow.realtime.channel.util.impl.JreChannelFactory;
import com.goodow.realtime.extensions.android.http.AndroidHttpTransport;

import com.google.inject.Singleton;

import android.os.Handler;
import android.os.Looper;

@Singleton
public class AndroidChannelFactory extends JreChannelFactory {
  @Override
  public String getDefaultUserAgent() {
    return System.getProperty("http.agent");
  }

  @Override
  public HttpTransport getHttpTransport() {
    return new AndroidHttpTransport();
  }

  @Override
  public void scheduleDeferred(Runnable cmd) {
    Handler handler = new Handler(Looper.getMainLooper());
    handler.post(cmd);
  }

  @Override
  public void scheduleFixedDelay(Runnable cmd, int delayMs) {
    Handler handler = new Handler();
    handler.postDelayed(cmd, delayMs);
  }
}
