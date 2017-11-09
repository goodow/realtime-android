package com.goodow.realtime.android.mvp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;

import com.goodow.realtime.android.mvp.util.CustomClassMapper;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by larry on 2017/11/7.
 */

public class RouteBuilder {
  static final String ACTIVITY_ID = RouteBuilder.class.getName();
  private Object data;
  private int flags = -1;         // Flags of route

  /**
   * Split query parameters
   *
   * @param rawUri raw uri
   * @return map with params
   */
  private static Map<String, String> splitQueryParameters(Uri rawUri) {
    String query = rawUri.getEncodedQuery();

    if (query == null) {
      return Collections.emptyMap();
    }

    Map<String, String> paramMap = new LinkedHashMap<>();
    int start = 0;
    do {
      int next = query.indexOf('&', start);
      int end = (next == -1) ? query.length() : next;

      int separator = query.indexOf('=', start);
      if (separator > end || separator == -1) {
        separator = end;
      }

      String name = query.substring(start, separator);

      if (!android.text.TextUtils.isEmpty(name)) {
        String value = (separator == end ? "" : query.substring(separator + 1, end));
        paramMap.put(Uri.decode(name), Uri.decode(value));
      }

      // Move start to end of name.
      start = end + 1;
    } while (start < query.length());

    return Collections.unmodifiableMap(paramMap);
  }


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
    Uri uri = Uri.parse(url);
    if (null == uri || uri.toString().length() == 0) {
      throw new RuntimeException("url Parameter invalid!");
    }
    Map<String, String> parameters = this.splitQueryParameters(uri);
    if (parameters.size() > 0) {
      if (this.data == null) {
        this.data = parameters;
      } else if (this.data instanceof Map) {
        ((Map) this.data).putAll(parameters);
      }
    }
    if (this.data instanceof Map) {
      parameters = (Map<String, String>) this.data;
    }

    if (parameters.containsKey("viewOpt.flags")) {
      this.flags = Integer.parseInt(parameters.get("viewOpt.flags"));
    }

    String className = uri.getLastPathSegment();
    if (!className.endsWith(Activity.class.getSimpleName())) {
      className = className + Activity.class.getSimpleName();
    }
    String rootPackageName = Router.getInstance().context.getPackageName();
    String packageName;
    if (parameters.containsKey("viewOpt.package")) {
      packageName = parameters.get("viewOpt.package");
      if (packageName.startsWith(".")) {
        packageName = rootPackageName + packageName;
      }
    } else if (!className.startsWith(rootPackageName)) {
      packageName = rootPackageName;
    } else {
      packageName = "";
    }
    className = packageName + (className.startsWith(".") ? "" : ".") + className;
    Class<?> activityClz;
    try {
      activityClz = Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(e);
    }
    if (!Activity.class.isAssignableFrom(activityClz)) {
      throw new RuntimeException("className Parameter invalid!");
    }

    if (this.data instanceof Map) {
      String viewModelName = className.substring(0, className.length() - Activity.class.getSimpleName().length()) + "ViewModel";
      try {
        Class viewModelClz = Class.forName(viewModelName);
        Object viewModel = CustomClassMapper.convertToCustomClass(this.data, viewModelClz);
        this.data = viewModel;
      } catch (ClassNotFoundException e) {
      }
    }

    this.goToClass((Class<? extends Activity>) activityClz);
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
