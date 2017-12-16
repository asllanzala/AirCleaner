package com.honeywell.hch.airtouchv3.lib.util;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class JSONSerializer {

    private static final String LOG_TAG = JSONSerializer.class.getSimpleName();

    /**
     * Parse clock template according to file name in "template/"
     * directory.</br> <b>Note:</b> If exception is thrown, it will return null.
     * 
     */
    public static JSONObject parseJSONData(String jsonData) {
        Log.v(LOG_TAG, "parseJSONData by file name start");
        try {
            JSONObject json = new JSONObject(jsonData);
            //Log.v(LOG_TAG, "parseJSONData: " + json);
            Log.v(LOG_TAG, "parseJSONData successed");
            return json;
        } catch (JSONException e) {
            Log.e(LOG_TAG, "parseJSONData.JSONException: " + e.getMessage());
            return null;
        }
    }

    /**
     * Deserializes json data to object.
     * 
     * @param object
     * @param json
     */
    public static <T> void deserializeJSONObject(T object, JSONObject json) {
        if (object == null || json == null)
        {
            return;
        }
        Class<?> objectClass = object.getClass();
        Class<?> superClass = objectClass.getSuperclass();
        Field[] fields = null;
        if (superClass != null) {
            fields = superClass.getDeclaredFields();
            for (Field field : fields) {
                writeValue(field, object, json);
            }
        }
        fields = objectClass.getDeclaredFields();
        for (Field field : fields) {
            writeValue(field, object, json);
        }
    }

    /**
     * Deserializes json array data to list.
     * 
     * @param elementClassType
     * @param array
     * @return
     */
    public static <T> List<T> deserializeJSONArray(Class<T> elementClassType,
            JSONArray array) {
        List<T> list = new ArrayList<T>();
        if (elementClassType == null || array == null || array == null || array.length() == 0) {
            return list;
        }
        for (int i = 0; i < array.length(); i++) {
            try {
                T object = elementClassType.newInstance();
                deserializeJSONObject(object, array.getJSONObject(i));
                list.add(object);
            } catch (JSONException e) {
                //Log.e(LOG_TAG, "deserializeJSONArray.JSONException: " + e.getMessage());
            	// keep it empty since we know it is the issue for not existed value
            	// continue the loop
            	
            } catch (IllegalAccessException e) {
                Log.e(LOG_TAG, "deserializeJSONArray.IllegalAccessException: "
                        + e.getMessage());
                break;
            } catch (InstantiationException e) {
                Log.e(LOG_TAG, "deserializeJSONArray.InstantiationException: "
                        + e.getMessage());
                break;
            }
        }
        return list;
    }

    /**
     * Convert input stream to string.
     * 
     * @param fileInputStream
     * @return
     * @throws java.io.IOException
     */
    private static String inputStream2String(InputStream fileInputStream)
            throws IOException {
        int buffersize = fileInputStream.available(); // Get the length of input
                                                      // stream
        byte buffer[] = new byte[buffersize];
        fileInputStream.read(buffer);
        fileInputStream.close();
        String result = new String(buffer);

        return result;
    }

    private static <T> void writeValue(Field field, T object, JSONObject json) {
        Object value = null;
        try {
            Class<?> actualClass = field.getType();
            String name = field.getName();
            field.setAccessible(true);
            if (actualClass == int.class || actualClass == Integer.class) {
                value = json.optInt(name);
            } else if (actualClass == String.class || actualClass == Integer.class) {
                value = json.optString(name);
            } else if (actualClass == boolean.class || actualClass == Boolean.class) {
                value = json.optBoolean(name);
            } else if (actualClass == double.class || actualClass == Double.class) {
                value = json.optDouble(name);
            } else if (actualClass == long.class || actualClass == Long.class) {
                value = json.optLong(name);
            } else if (List.class.isAssignableFrom(actualClass)) {
                JSONSerializeAnnotation attr = field
                        .getAnnotation(JSONSerializeAnnotation.class);
                if (attr != null) {
                    Class<?> subClass = attr.actualClass();
                    value = deserializeJSONArray(subClass,
                            json.getJSONArray(name));
                }
            } else if (actualClass == JSONObject.class) {
                value = json.getJSONObject(name);
            } else if (actualClass == JSONArray.class) {
                value = json.getJSONArray(name);
            } else if (actualClass == Object.class) {
                value = field.get(object);
                if (value != null)
                {
                    if (value instanceof String)
                    {
                        value = json.getJSONObject(name).toString();
                    }
                    else
                    {
                    	Log.e(LOG_TAG, "name = " + name + "; value = " + value);
                        deserializeJSONObject(value, json.getJSONObject(name));
                    }
                }
                else
                {
                    value = json.opt(name);
                }
            } else {
                value = field.get(object);
                // when the field object is null, new the object
                if (value == null) {
                    value = actualClass.newInstance();
                }
                deserializeJSONObject(value, json.getJSONObject(name));

            }
            field.set(object, value);
        } catch (IllegalAccessException e) {
        	e.printStackTrace();
            Log.w(LOG_TAG,
                    "deserialize.IllegalAccessException: " + e.getMessage());
        } catch (InstantiationException e) {
        	e.printStackTrace();
            Log.w(LOG_TAG,
                    "deserialize.InstantiationException: " + e.getMessage());
        } catch (JSONException e) {
        	e.printStackTrace();
            Log.w(LOG_TAG, "deserialize.JSONException: " + e.getMessage());
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface JSONSerializeAnnotation {
        public Class<?> actualClass();
    }
}
