package com.goodow.realtime.channel.impl;

import com.goodow.realtime.channel.Message;
import com.goodow.realtime.channel.AsyncResult;

public class AsyncResultImpl<T> implements AsyncResult<T> {
  private Throwable cause;
  private T result;

  public AsyncResultImpl(Message message) {
    if (message.payload() instanceof Throwable) {
      this.cause = (Throwable)message.payload();
    }
    this.result = (T) message;
  }

  @Override
  public Throwable cause() {
    return cause;
  }

  @Override
  public boolean failed() {
    return cause != null;
  }

  @Override
  public T result() {
    return result;
  }
}
