package com.goodow.realtime.android.demo.test;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.goodow.realtime.android.mvp.Router;

/**
 * Created by larry on 2017/11/8.
 */

public class TestCActivity extends Activity {

  public String title;
  public int id;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Router.getInstance().inject(this);
    String title = this.title;
  }

}
