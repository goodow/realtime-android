package com.goodow.realtime.android.mvp.util;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * Created by larry on 2017/11/9.
 *
 * Helper class to convert from/to JSON strings.
 */
public class JsonMapper {

  public static String serializeJson(Map<String, Object> object) throws IOException {
    return serializeJsonValue(object);
  }

  @SuppressWarnings("unchecked")
  public static String serializeJsonValue(Object object) throws IOException {
    if (object == null) {
      return "null";
    } else if (object instanceof String) {
      return JSONObject.quote((String) object);
    } else if (object instanceof Number) {
      try {
        return JSONObject.numberToString((Number) object);
      } catch (JSONException e) {
        throw new IOException("Could not serialize number", e);
      }
    } else if (object instanceof Boolean) {
      return ((Boolean) object) ? "true" : "false";
    } else {
      try {
        JSONStringer stringer = new JSONStringer();
        serializeJsonValue(object, stringer);
        return stringer.toString();
      } catch (JSONException e) {
        throw new IOException("Failed to serialize JSON", e);
      }
    }
  }

  private static void serializeJsonValue(Object object, JSONStringer stringer)
      throws IOException, JSONException {
    if (object instanceof Map) {
      stringer.object();
      @SuppressWarnings("unchecked")
      Map<String, Object> map = (Map<String, Object>) object;
      for (Map.Entry<String, Object> entry : map.entrySet()) {
        stringer.key(entry.getKey());
        serializeJsonValue(entry.getValue(), stringer);
      }
      stringer.endObject();
    } else if (object instanceof Collection) {
      Collection<?> collection = (Collection<?>) object;
      stringer.array();
      for (Object entry : collection) {
        serializeJsonValue(entry, stringer);
      }
      stringer.endArray();
    } else {
      stringer.value(object);
    }
  }
}
