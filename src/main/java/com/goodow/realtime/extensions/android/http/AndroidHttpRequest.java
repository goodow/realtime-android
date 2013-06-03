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
package com.goodow.realtime.extensions.android.http;

import com.goodow.realtime.channel.http.HttpRequest;
import com.goodow.realtime.channel.http.HttpRequestCallback;

import com.google.api.client.util.Preconditions;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;

import android.os.AsyncTask;

final class AndroidHttpRequest extends HttpRequest {

  private class RequestTask extends AsyncTask<Void, Void, AndroidHttpResponse> {
    Exception exceptionThrown = null;
    private final HttpRequestCallback callback;

    public RequestTask(HttpRequestCallback callback) {
      this.callback = callback;
    }

    @Override
    protected AndroidHttpResponse doInBackground(Void... params) {
      try {
        // write content
        String content = getContent();
        if (content != null) {
          String contentType = getContentType();
          if (contentType != null) {
            addHeader("Content-Type", contentType);
          }
          String contentEncoding = getContentEncoding();
          if (contentEncoding != null) {
            addHeader("Content-Encoding", contentEncoding);
          }
          long contentLength = getContentLength();
          if (contentLength >= 0) {
            addHeader("Content-Length", Long.toString(contentLength));
          }
          String requestMethod = connection.getRequestMethod();
          if ("POST".equals(requestMethod) || "PUT".equals(requestMethod)) {
            connection.setDoOutput(true);
            // see http://developer.android.com/reference/java/net/HttpURLConnection.html
            if (contentLength >= 0 && contentLength <= Integer.MAX_VALUE) {
              connection.setFixedLengthStreamingMode((int) contentLength);
            } else {
              connection.setChunkedStreamingMode(0);
            }
            OutputStream out = connection.getOutputStream();
            String encoding = getContentEncoding();
            try {
              out.write(content.getBytes(encoding == null ? "UTF-8" : encoding));
            } finally {
              out.close();
            }
          } else {
            // cannot call setDoOutput(true) because it would change a GET method to POST
            // for HEAD, OPTIONS, DELETE, or TRACE it would throw an exceptions
            Preconditions.checkArgument(contentLength == 0,
                "%s with non-zero content length is not supported", requestMethod);
          }
        }
        // connect
        boolean successfulConnection = false;
        try {
          connection.connect();
          AndroidHttpResponse response = new AndroidHttpResponse(connection);
          successfulConnection = true;
          return response;
        } finally {
          if (!successfulConnection) {
            connection.disconnect();
          }
        }
      } catch (IOException e) {
        exceptionThrown = e;
        return null;
        // Handle exception in PostExecute
      }
    }

    @Override
    protected void onPostExecute(AndroidHttpResponse result) {
      // Check if exception was thrown
      if (exceptionThrown != null) {
        callback.onFailure(exceptionThrown);
      } else {
        callback.onResponse(result);
      }
    }
  }

  private final HttpURLConnection connection;

  /**
   * @param connection HTTP URL connection
   */
  AndroidHttpRequest(HttpURLConnection connection) {
    this.connection = connection;
    connection.setInstanceFollowRedirects(false);
  }

  @Override
  public void addHeader(String name, String value) {
    connection.addRequestProperty(name, value);
  }

  @Override
  public void executeAsync(HttpRequestCallback callback) throws IOException {
    new RequestTask(callback).execute();
  }

  @Override
  public void setTimeout(int connectTimeout, int readTimeout) {
    connection.setReadTimeout(readTimeout);
    connection.setConnectTimeout(connectTimeout);
  }
}
