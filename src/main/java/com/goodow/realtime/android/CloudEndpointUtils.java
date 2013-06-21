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

import com.google.api.client.googleapis.services.AbstractGoogleClient;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;

/**
 * Common utilities for working with Cloud Endpoints.
 * 
 * If you'd like to test using a locally-running version of your App Engine backend (i.e. running on
 * the Development App Server), you need to set LOCAL_ANDROID_RUN to 'true'.
 * 
 * See the documentation at http://developers.google.com/eclipse/docs/cloud_endpoints for more
 * information.
 */
public class CloudEndpointUtils {

  // static {
  // System.setProperty("http.keepAlive", "false");
  // }

  /**
   * Updates the Google client builder to connect the appropriate server based on whether
   * LOCAL_ANDROID_RUN is true or false.
   * 
   * @param builder Google client builder
   * @return same Google client builder
   */
  public static <B extends AbstractGoogleClient.Builder> B updateBuilder(B builder) {
    // only enable GZip when connecting to remote server
    final boolean enableGZip = builder.getRootUrl().startsWith("https:");

    builder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
      @Override
      public void initialize(AbstractGoogleClientRequest<?> request) throws IOException {
        if (!enableGZip) {
          request.setDisableGZipContent(true);
        }
      }
    });

    return builder;
  }
}
