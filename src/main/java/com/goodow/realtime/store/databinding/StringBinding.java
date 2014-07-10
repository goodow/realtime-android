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

import com.goodow.realtime.store.CollaborativeString;

/**
 * A binding between a collaborative string in the data model and a text view.
 */
public abstract class StringBinding {
  private final CollaborativeString collaborativeString;
  private final TextView view;

  /**
   * @param collaborativeString The collaborative string to bind.
   * @param view The text view to bind.
   */
  public StringBinding(CollaborativeString collaborativeString, TextView view) {
    this.collaborativeString = collaborativeString;
    this.view = view;
  }

  /**
   * @return The collaborative string that this registration binds to the text view.
   */
  public CollaborativeString collaborativeObject() {
    return collaborativeString;
  }

  /**
   * @return The text view that this registration binds to the collaborative string.
   */
  public TextView view() {
    return view;
  }

  /**
   * Unbinds the text view from the collaborative string.
   */
  public abstract void unbind();
}
