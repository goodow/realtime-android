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

import com.goodow.api.services.device.Device;
import com.goodow.realtime.Realtime;
import com.goodow.realtime.android.gcm.GCMIntentService;
import com.goodow.realtime.channel.util.ChannelFactory;
import com.goodow.realtime.channel.util.ChannelNative;
import com.goodow.realtime.extensions.android.AndroidChannelFactory;
import com.goodow.realtime.model.util.ModelFactory;
import com.goodow.realtime.model.util.ModelNative;
import com.goodow.realtime.model.util.impl.JreModelFactory;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import android.app.Application;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;

public class RealtimeModule extends AbstractModule {
  private static final String SERVER_ADDRESS = "com.goodow.realtime.serverAddress";
  private static final String REALTIME_SERVER = "http://realtime.goodow.com";

  public static String getEndpointRootUrl(String serverAddress) {
    if (serverAddress.endsWith(".goodow.com")) {
      return serverAddress + "/ah/api/";
    } else {
      return serverAddress + "/_ah/api/";
    }
  }

  @Override
  protected void configure() {
    requestStaticInjection(GCMIntentService.class);
    bind(ModelFactory.class).to(JreModelFactory.class);
    bind(ChannelFactory.class).to(AndroidChannelFactory.class);
    requestStaticInjection(ModelNative.class, ChannelNative.class);
  }

  @Provides
  @Singleton
  Device provideDevice(@ServerAddress String serverAddress) {
    Device.Builder endpointBuilder =
        new Device.Builder(AndroidHttp.newCompatibleTransport(), new JacksonFactory(),
            new HttpRequestInitializer() {
              @Override
              public void initialize(HttpRequest httpRequest) {
              }
            });
    endpointBuilder.setRootUrl(getEndpointRootUrl(serverAddress));
    return CloudEndpointUtils.updateBuilder(endpointBuilder).build();
  }

  @Provides
  @Singleton
  @ServerAddress
  String provideServerAddress(Application application) {
    Resources resources = application.getResources();
    int identifier =
        resources.getIdentifier(SERVER_ADDRESS, "string", application.getPackageName());
    String serverAddress = null;
    try {
      serverAddress = resources.getString(identifier);
    } catch (NotFoundException e) {
      serverAddress = REALTIME_SERVER;
    }
    Realtime.setServerAddress(serverAddress);
    return serverAddress;
  }
}
