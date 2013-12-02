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

import com.goodow.realtime.core.Platform;
import com.goodow.realtime.core.VoidHandler;
import com.goodow.realtime.core.WebSocket;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

public class JavaWebSocket implements WebSocket {
  private static final Logger log = Logger.getLogger(JavaWebSocket.class.getName());
  private static Charset charset = Charset.forName("UTF-8");
  private static CharsetDecoder decoder = charset.newDecoder();

  private static String toString(ByteBuffer buffer) throws CharacterCodingException {
    String data = null;
    int old_position = buffer.position();
    data = decoder.decode(buffer).toString();
    // reset buffer's position to its original so it is not altered:
    buffer.position(old_position);
    return data;
  }

  private final WebSocketClient socket;

  private WebSocketHandler eventHandler;

  public JavaWebSocket(final Platform platform, String uri) {
    URI juri = null;
    try {
      juri = new URI(uri);
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }

    socket = new WebSocketClient(juri, new Draft_17()) {
      @Override
      public void onClose(int arg0, String arg1, boolean arg2) {
        if (eventHandler == null) {
          return;
        }
        platform.scheduleDeferred(new VoidHandler() {
          @Override
          public void handle() {
            eventHandler.onClose();
          }
        });
      }

      @Override
      public void onError(final Exception e) {
        log.log(Level.SEVERE, "Error occured when processing WebSocket", e);
        if (eventHandler == null) {
          return;
        }
        platform.scheduleDeferred(new VoidHandler() {
          @Override
          public void handle() {
            String message = e.getMessage();
            eventHandler.onError(message == null ? e.getClass().getSimpleName() : message);
          }
        });
      }

      @Override
      public void onMessage(final ByteBuffer buffer) {
        if (eventHandler == null) {
          return;
        }
        platform.scheduleDeferred(new VoidHandler() {
          @Override
          public void handle() {
            try {
              eventHandler.onMessage(JavaWebSocket.toString(buffer));
            } catch (CharacterCodingException e) {
              onError(e);
            }
          }
        });
      }

      @Override
      public void onMessage(final String msg) {
        if (eventHandler == null) {
          return;
        }
        platform.scheduleDeferred(new VoidHandler() {
          @Override
          public void handle() {
            eventHandler.onMessage(msg);
          }
        });
      }

      @Override
      public void onOpen(ServerHandshake handshake) {
        if (eventHandler == null) {
          return;
        }
        platform.scheduleDeferred(new VoidHandler() {
          @Override
          public void handle() {
            eventHandler.onOpen();
          }
        });
      }
    };
    socket.connect();
  }

  @Override
  public void close() {
    socket.close();
  }

  @Override
  public void send(String data) {
    try {
      socket.getConnection().send(data);
    } catch (Throwable e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void setListen(WebSocketHandler handler) {
    this.eventHandler = handler;
  }
}