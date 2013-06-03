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

import com.goodow.realtime.channel.http.HttpResponse;

import com.google.api.client.http.HttpStatusCodes;
import com.google.common.io.CharStreams;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

final class AndroidHttpResponse extends HttpResponse {

  private final HttpURLConnection connection;
  private final int responseCode;
  private final String responseMessage;
  private final ArrayList<String> headerNames = new ArrayList<String>();
  private final ArrayList<String> headerValues = new ArrayList<String>();
  private final String content;

  AndroidHttpResponse(HttpURLConnection connection) throws IOException {
    this.connection = connection;
    int responseCode = connection.getResponseCode();
    this.responseCode = responseCode == -1 ? 0 : responseCode;
    responseMessage = connection.getResponseMessage();
    List<String> headerNames = this.headerNames;
    List<String> headerValues = this.headerValues;
    for (Map.Entry<String, List<String>> entry : connection.getHeaderFields().entrySet()) {
      String key = entry.getKey();
      if (key != null) {
        for (String value : entry.getValue()) {
          if (value != null) {
            headerNames.add(key);
            headerValues.add(value);
          }
        }
      }
    }

    try {
      InputStream inputStream =
          HttpStatusCodes.isSuccess(responseCode) ? connection.getInputStream() : connection
              .getErrorStream();
      String encoding = getContentEncoding();
      content =
          CharStreams.toString(new InputStreamReader(inputStream, encoding == null ? "UTF-8"
              : encoding));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Closes the connection to the HTTP server.
   * 
   * @since 1.4
   */
  @Override
  public void disconnect() {
    connection.disconnect();
  }

  @Override
  public String getContent() {
    return content;
  }

  @Override
  public String getContentEncoding() {
    return connection.getContentEncoding();
  }

  @Override
  public long getContentLength() {
    String string = connection.getHeaderField("Content-Length");
    return string == null ? -1 : Long.parseLong(string);
  }

  @Override
  public String getContentType() {
    return connection.getHeaderField("Content-Type");
  }

  @Override
  public int getHeaderCount() {
    return headerNames.size();
  }

  @Override
  public String getHeaderName(int index) {
    return headerNames.get(index);
  }

  @Override
  public String getHeaderValue(int index) {
    return headerValues.get(index);
  }

  @Override
  public String getReasonPhrase() {
    return responseMessage;
  }

  @Override
  public int getStatusCode() {
    return responseCode;
  }

  @Override
  public String getStatusLine() {
    String result = connection.getHeaderField(0);
    return result != null && result.startsWith("HTTP/1.") ? result : null;
  }
}
