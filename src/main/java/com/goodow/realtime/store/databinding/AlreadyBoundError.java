/*
 * Copyright 2014 Goodow.com
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
package com.goodow.realtime.store.databinding;

import android.widget.TextView;

/**
 * An error that is thrown when attempting to bind a view which has already been bound to a
 * collaborative value.
 */
public class AlreadyBoundError extends Error {
  private final TextView view;
  private String name;

  /**
   * @param view The view that was already bound.
   */
  public AlreadyBoundError(TextView view) {
    this.view = view;
  }

  /**
   * @return The name of this error.
   */
  public String name() {
    return name;
  }

  /**
   * @return The view that was already bound.
   */
  public TextView view() {
    return view;
  }
}
