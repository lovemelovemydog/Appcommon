package com.common.app.common.util;

import org.json.JSONObject;

/**
 * Created by fangzhu on 2015/3/25.
 */
public class JSONObjectUtil {

    public static int getInt (JSONObject object, String name, int deaultVal) {
        try {
            if (object == null)
                return deaultVal;
            if (object.has(name))
                return object.getInt(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deaultVal;
    }

    public static long getLong (JSONObject object, String name, long deaultVal) {
        try {
            if (object == null)
                return deaultVal;
            if (object.has(name))
                return object.getLong(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deaultVal;
    }

    public static String getString (JSONObject object, String name, String deaultVal) {
        try {
            if (object == null)
                return deaultVal;
            if (object.has(name)) {
                String val = object.getString(name);
                if (val == null || val.trim().equalsIgnoreCase("null"))
                    return deaultVal;
                return val;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return deaultVal;
    }

    public static boolean getBoolean (JSONObject object, String name, boolean deaultVal) {
        try {
            if (object == null)
                return deaultVal;
            if (object.has(name))
                return object.getBoolean(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deaultVal;
    }

}
