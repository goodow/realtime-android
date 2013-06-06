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
package com.goodow.realtime.android;

import com.goodow.realtime.channel.util.ChannelFactory;
import com.goodow.realtime.channel.util.ChannelNative;
import com.goodow.realtime.extensions.android.AndroidChannelFactory;
import com.goodow.realtime.model.util.ModelFactory;
import com.goodow.realtime.model.util.ModelNative;
import com.goodow.realtime.model.util.impl.JreModelFactory;

import com.google.inject.AbstractModule;

public class RealtimeModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(ModelFactory.class).to(JreModelFactory.class);
    bind(ChannelFactory.class).to(AndroidChannelFactory.class);
    requestStaticInjection(ModelNative.class, ChannelNative.class);
  }
}
