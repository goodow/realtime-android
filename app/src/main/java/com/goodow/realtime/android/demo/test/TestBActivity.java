package com.goodow.realtime.android.demo.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.goodow.realtime.android.mvp.Router;

/**
 * Created by larry on 2017/11/8.
 */

public class TestBActivity extends Activity {

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    TestBViewModel data = Router.getInstance().getData(getIntent());
    if (data != null) {
      int id = data.id;
      String title = data.title;
    }
  }

}
