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
package com.goodow.realtime.android.gcm;

import com.goodow.api.services.device.Device;
import com.goodow.api.services.device.model.DeviceInfo;
import com.goodow.realtime.channel.ChannelDemuxer;
import com.goodow.realtime.channel.constant.MessageType;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;
import com.google.inject.Inject;

import java.io.IOException;
import java.net.URLEncoder;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

/**
 * This class is started up as a service of the Android application. It listens for Google Cloud
 * Messaging (GCM) messages directed to this device.
 * 
 * When the device is successfully registered for GCM, a message is sent to the App Engine backend
 * via Cloud Endpoints, indicating that it wants to receive broadcast messages from the it.
 * 
 * Before registering for GCM, you have to create a project in Google's Cloud Console
 * (https://code.google.com/apis/console). In this project, you'll have to enable the
 * "Google Cloud Messaging for Android" Service.
 * 
 * Once you have set up a project and enabled GCM, you'll have to set the PROJECT_NUMBER field to
 * the project number mentioned in the "Overview" page.
 * 
 * See the documentation at http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class GCMIntentService extends GCMBaseIntentService {
  public static final String DOWNLOAD_ACTION = "com.goodow.realtime.android.action.DOWNLOAD";
  /*
   * Set this to a valid project number. See
   * http://developers.google.com/eclipse/docs/cloud_endpoint for more information.
   */
  public static final String PROJECT_NUMBER = "158501807005";
  @Inject
  private static Device device;

  /**
   * Register the device for GCM.
   * 
   * @param mContext the activity's context.
   */
  public static void register(Context mContext) {
    GCMRegistrar.checkDevice(mContext);
    GCMRegistrar.checkManifest(mContext);
    GCMRegistrar.register(mContext, PROJECT_NUMBER);
  }

  /**
   * Unregister the device from the GCM service.
   * 
   * @param mContext the activity's context.
   */
  public static void unregister(Context mContext) {
    GCMRegistrar.unregister(mContext);
  }

  public GCMIntentService() {
    super(PROJECT_NUMBER);
  }

  /**
   * Called on registration error. This is called in the context of a Service - no dialog or UI.
   * 
   * @param context the Context
   * @param errorId an error message
   */
  @Override
  public void onError(Context context, String errorId) {
    Log.e(GCMIntentService.class.getName(),
        "Registration with Google Cloud Messaging...FAILED!\n\n"
            + "A Google Cloud Messaging registration error occured (errorid: " + errorId + "). ");
  }

  /**
   * Called when a cloud message has been received.
   */
  @Override
  public void onMessage(Context context, final Intent intent) {
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        MessageType[] messageTypes = MessageType.values();
        for (MessageType type : messageTypes) {
          String key = type.key();
          if (!intent.hasExtra(key)) {
            continue;
          }
          String msg = intent.getStringExtra(key);
          switch (type) {
            case REALTIME:
              ChannelDemuxer.get().onMessage(msg);
              break;
            case DOWNLOAD:
              Intent downloadIntent = new Intent(DOWNLOAD_ACTION);
              downloadIntent.putExtra(MessageType.DOWNLOAD.name(), msg);
              sendBroadcast(downloadIntent);
              break;
            default:
              break;
          }
        }
      }
    });
  }

  /**
   * Called back when a registration token has been received from the Google Cloud Messaging
   * service.
   * 
   * @param context the Context
   */
  @Override
  public void onRegistered(Context context, String registration) {
    /*
     * This is some special exception-handling code that we're using to work around a problem with
     * the DevAppServer and methods that return null in App Engine 1.7.5.
     */
    boolean alreadyRegisteredWithEndpointServer = false;

    DeviceInfo existingDevice = null;
    try {
      /*
       * Using cloud endpoints, see if the device has already been registered with the backend
       */
      existingDevice = device.getDeviceInfo(registration).execute();

      if (existingDevice != null && registration.equals(existingDevice.getId())) {
        alreadyRegisteredWithEndpointServer = true;
      }
    } catch (IOException e) {
      // Ignore
    }

    try {
      String sessionId = ChannelDemuxer.getSessionId();
      if (!alreadyRegisteredWithEndpointServer) {
        Log.i(GCMIntentService.class.getName(),
            "Registration with Google Cloud Messaging...SUCCEEDED!");
        /*
         * We are not registered as yet. Send an endpoint message containing the GCM registration id
         * and some of the device's product information over to the backend. Then, we'll be
         * registered.
         */
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.setSessionId(sessionId);
        String description =
            URLEncoder.encode(android.os.Build.MANUFACTURER + " " + android.os.Build.PRODUCT,
                "UTF-8");
        device.insertDeviceInfo(
            deviceInfo.setId(registration).setTimestamp(System.currentTimeMillis()).setInformation(
                description)).execute();
      } else {
        existingDevice.setSessionId(sessionId).setTimestamp(System.currentTimeMillis());
        device.deviceEndpoint().updateDeviceInfo(existingDevice).execute();
      }
    } catch (IOException e) {
      Log.e(GCMIntentService.class.getName(),
          "Exception received when attempting to register with server at " + device.getRootUrl(), e);
      return;
    }
    Log.i(GCMIntentService.class.getName(), "Registration with Endpoints Server...SUCCEEDED!");
  }

  /**
   * Called back when the Google Cloud Messaging service has unregistered the device.
   * 
   * @param context the Context
   */
  @Override
  protected void onUnregistered(Context context, String registrationId) {
    if (registrationId != null && registrationId.length() > 0) {
      Log.i(GCMIntentService.class.getName(),
          "De-registration with Google Cloud Messaging....SUCCEEDED!");
      try {
        device.removeDeviceInfo(registrationId).execute();
      } catch (IOException e) {
        Log.e(GCMIntentService.class.getName(),
            "Exception received when attempting to unregister with server at "
                + device.getRootUrl(), e);
        return;
      }
    }
  }
}
