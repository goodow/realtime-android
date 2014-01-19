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
import com.goodow.realtime.core.Scheduler;

public class JavaPlatform implements PlatformFactory {
  /**
   * Registers the Java platform with a default configuration.
   */
  public static void register() {
    Platform.setFactory(new JavaPlatform());
  }

  protected final JavaNet net;
  protected final JavaScheduler scheduler;

  protected JavaPlatform(JavaScheduler scheduler) {
    net = new JavaNet();
    this.scheduler = scheduler;
  }

  private JavaPlatform() {
    this(new JavaScheduler());
  }

  @Override
  public Net net() {
    return net;
  }

  @Override
  public Scheduler scheduler() {
    return scheduler;
  }

  @Override
  public Type type() {
    return Type.JAVA;
  }
}