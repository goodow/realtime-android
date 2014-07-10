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

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

import com.goodow.realtime.core.Handler;
import com.goodow.realtime.core.Registration;
import com.goodow.realtime.store.CollaborativeString;
import com.goodow.realtime.store.TextDeletedEvent;
import com.goodow.realtime.store.TextInsertedEvent;

/**
 * A namespace that includes classes and methods for binding collaborative objects to UI views.
 */
public class Databinding {
  /**
   * Binds a text view to an collaborative string. Once bound, any change to the
   * collaborative string (including changes from other remote collaborators) is immediately
   * displayed in the text editing view. Conversely, any change in the text editing view is
   * reflected in the data model.
   * 
   * @param string The collaborative string to bind.
   * @param textView The text view to bind.
   * @return A binding registration that can be later used to remove the binding.
   * @throws AlreadyBoundError
   */
  public static StringBinding bindString(final CollaborativeString string, final TextView textView)
    throws AlreadyBoundError {

    final TextWatcher textWatcher = new TextWatcher() {
      @Override
      public void afterTextChanged(Editable arg0) {
      }

      @Override
      public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {
        string.setText(textView.getText().toString());
      }
    };
    textView.addTextChangedListener(textWatcher);

    final Registration textDeletedRegistration = string.onTextDeleted(new Handler<TextDeletedEvent>() {
      @Override
      public void handle(TextDeletedEvent event) {
        if (!event.isLocal()) {
          Editable editable = textView.getEditableText();
          if (editable != null) {
            editable.delete(event.index(), event.index() + event.text().length());
          } else {
            textView.setText(string.getText());
          }
        }
      }
    });
    final Registration textInsertedRegistration = string.onTextInserted(new Handler<TextInsertedEvent>() {
      @Override
      public void handle(TextInsertedEvent event) {
        if (!event.isLocal()) {
          Editable editable = textView.getEditableText();
          if (editable != null) {
            editable.insert(event.index(), event.text());
          } else {
            textView.setText(string.getText());
          }
        }
      }
    });

    return new StringBinding(string, textView) {
      @Override
      public void unbind() {
        textView.removeTextChangedListener(textWatcher);
        textDeletedRegistration.unregister();
        textInsertedRegistration.unregister();
      }
    };
  }
}
