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
package com.goodow.realtime.extensions.android.http;

import com.goodow.realtime.channel.http.HttpRequest;
import com.goodow.realtime.channel.http.HttpRequestCallback;
import com.goodow.realtime.channel.http.HttpTransport;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;

import java.io.IOException;
import java.util.logging.Logger;

import android.os.AsyncTask;

final class AndroidHttpRequest implements HttpRequest {
  private class RequestTask extends AsyncTask<Void, Void, AndroidHttpResponse> {
    private Exception exceptionThrown = null;
    private final HttpRequestCallback callback;

    public RequestTask(HttpRequestCallback callback) {
      this.callback = callback;
    }

    @Override
    protected AndroidHttpResponse doInBackground(Void... params) {
      LOG.config("doInBackground, current tasks: " + numAsyncTasks);
      HttpResponse httpResponse = null;
      try {
        // write content
        httpResponse = httpRequest.execute();
        return new AndroidHttpResponse(httpResponse);
      } catch (IOException e) {
        exceptionThrown = e;
        return null;
        // Handle exception in PostExecute
      } finally {
        try {
          if (httpResponse != null) {
            httpResponse.disconnect();
          }
        } catch (IOException e) {
          exceptionThrown = e;
        }
      }
    }

    @Override
    protected void onPostExecute(AndroidHttpResponse result) {
      --numAsyncTasks;
      LOG.config("onPostExecute, result in tasks: " + numAsyncTasks);
      super.onPostExecute(result);
      // Check if exception was thrown
      if (exceptionThrown != null) {
        callback.onFailure(exceptionThrown);
      } else {
        callback.onResponse(result);
      }
    }

    @Override
    protected void onPreExecute() {
      LOG.config("executeAsync start, current tasks: " + numAsyncTasks);
      super.onPreExecute();
      numAsyncTasks++;
    }
  }

  private static final Logger LOG = Logger.getLogger(com.google.api.client.http.HttpTransport.class
      .getName());

  private static int numAsyncTasks;

  private static final com.google.api.client.http.HttpTransport HTTP_TRANSPORT = AndroidHttp
      .newCompatibleTransport();

  private final String method;
  private final String url;
  private com.google.api.client.http.HttpRequest httpRequest;

  AndroidHttpRequest(String method, String url) {
    this.method = method;
    this.url = url;
  }

  @Override
  public void executeAsync(HttpRequestCallback callback, String content) {
    HttpRequestFactory requestFactory = HTTP_TRANSPORT.createRequestFactory();
    try {
      httpRequest =
          requestFactory.buildRequest(method, new GenericUrl(HttpTransport.CHANNEL + url),
              content == null ? null : new ByteArrayContent("application/json; charset=utf-8",
                  content.getBytes("UTF-8")));
    } catch (IOException e) {
      callback.onFailure(e);
    }
    new RequestTask(callback).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
  }
}