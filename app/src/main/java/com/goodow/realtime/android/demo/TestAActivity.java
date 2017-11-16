package com.goodow.realtime.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.goodow.realtime.android.mvp.Router;

/**
 * Created by larry on 2017/11/7.
 */

public class TestAActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Object data = Router.getInstance().getData(this);
    if (data != null) {

    }
  }
}
