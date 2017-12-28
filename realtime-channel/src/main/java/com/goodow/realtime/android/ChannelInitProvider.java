package com.goodow.realtime.android;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.goodow.realtime.android.mvp.Router;
import com.goodow.realtime.channel.util.BusProvider;

import java.util.HashMap;

/**
 * Created by larry on 2017/12/11.
 */
@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1)
public class ChannelInitProvider extends ContentProvider {
  private static long elapsedTime; // App start

  public static void init(Application application) {
    application.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
      @Override
      public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

      }

      @Override
      public void onActivityStarted(Activity activity) {

      }

      @Override
      public void onActivityResumed(Activity activity) {
        if (elapsedTime > 0) {
          HashMap<String, Object> evt = new HashMap<>();
          evt.put("time_consuming", (double) (System.currentTimeMillis() - elapsedTime) / 1000.);
          elapsedTime = -1;
          BusProvider.get().publishLocal("logReport/app_launch", evt);
        }
      }

      @Override
      public void onActivityPaused(Activity activity) {

      }

      @Override
      public void onActivityStopped(Activity activity) {

      }

      @Override
      public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

      }

      @Override
      public void onActivityDestroyed(Activity activity) {

      }
    });
    Router.init(application);
  }

  public ChannelInitProvider() {
    elapsedTime = System.currentTimeMillis();
  }

  @Override
  public void attachInfo(Context context, ProviderInfo info) {
    super.attachInfo(context, info);
  }

  @Override
  public boolean onCreate() {
    return false;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }
}
