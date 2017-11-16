package com.goodow.realtime.android.mvp;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.goodow.realtime.android.mvp.util.BeanUtils;

import java.util.Map;
import java.util.WeakHashMap;

import static com.goodow.realtime.android.mvp.RouteBuilder.ACTIVITY_ID;

/**
 * Created by larry on 2017/11/7.
 */

public class Router {
  private volatile static Router instance = null;
  static Context context;
  static WeakHashMap<String, Object> intentCache = new WeakHashMap<>();

  /**
   * Init, it must be call before used router.
   */
  public static void init(Application application) {
    if (context != null) {
      return;
    }
    context = application;
  }

  /**
   * Get instance of router.
   * All feature U use, will be starts here.
   */
  public static Router getInstance() {
    if (context == null) {
      throw new RuntimeException("Router::Init::Invoke init(context) first!");
    }
    if (instance == null) {
      synchronized (Router.class) {
        if (instance == null) {
          instance = new Router();
        }
      }
    }
    return instance;
  }

  private Router() {
  }

  /**
   * Inject params.
   */
  public void inject(Activity activity) {
    Object data = this.getData(activity);
    if (data instanceof Map) {
      BeanUtils.populate(activity, (Map<String, ? extends Object>) data);
    }
  }

  public <T> T getData(Activity activity) {
    return (T) Router.intentCache.get(activity.getIntent().getStringExtra(ACTIVITY_ID));
  }

  public RouteBuilder withData(Object data) {
    return new RouteBuilder(data);
  }

  public void goToClass(Class<? extends Activity> activity) {
    new RouteBuilder(null).goToClass(activity);
  }

  public void goToUrl(String url) {
    new RouteBuilder(null).goToUrl(url);
  }

}
