package com.goodow.realtime.android.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import java.util.UUID;

/**
 * Created by larry on 2017/11/7.
 */

public class RouteBuilder {
  static final String ACTIVITY_ID = RouteBuilder.class.getName();
  private final Object data;
  private int flags = -1;         // Flags of route

  public RouteBuilder(Object data) {
    this.data = data;
  }

  public void goToClass(Class<? extends Activity> activity) {
    // Build intent
    final Context context = Router.getInstance().context;
    final Intent intent = new Intent(context, activity);

    // Navigation in main looper.
    new Handler(Looper.getMainLooper()).post(new Runnable() {
      @Override
      public void run() {
        if (data != null) {
          String activityId = UUID.randomUUID().toString();
          intent.putExtra(ACTIVITY_ID, activityId);
          Router.intentCache.put(activityId, data);
        }
        intent.setFlags(flags == -1 ? Intent.FLAG_ACTIVITY_NEW_TASK : flags);
        ActivityCompat.startActivity(context, intent, null);
      }
    });
  }

  public void goToUrl(String url) {

  }

  /**
   * Set special flags controlling how this intent is handled.  Most values
   * here depend on the type of component being executed by the Intent,
   * specifically the FLAG_ACTIVITY_* flags are all for use with
   * {@link Context#startActivity Context.startActivity()}.
   */
  public RouteBuilder withFlags(int flag) {
    this.flags = flag;
    return this;
  }
}
