package com.common.app.common;

public class Log {
	public final static boolean isLog = AppCommonStatic.DEBUG;

	public static void v(String TAG, String content) {
		if (isLog) {
			android.util.Log.v(TAG, content);
		}
	}

	public static void v(String TAG, String content, Throwable tr) {
		if (isLog) {
			android.util.Log.v(TAG, content, tr);
		}
	}

	public static void d(String TAG, String content) {
		if (isLog) {
			android.util.Log.d(TAG, content);
		}
	}

	public static void d(String TAG, String content, Throwable tr) {
		if (isLog) {
			android.util.Log.e(TAG, content, tr);
		}
	}

	public static void e(String TAG, String content) {
		if (isLog) {
			android.util.Log.e(TAG, content);
		}
	}

	public static void e(String TAG, String content, Throwable tr) {
		if (isLog) {
			android.util.Log.e(TAG, content, tr);
		}
	}

	public static void i(String TAG, String content) {
		if (isLog) {
			android.util.Log.i(TAG, content);
		}
	}

	public static void i(String TAG, String content, Throwable tr) {
		if (isLog) {
			android.util.Log.i(TAG, content, tr);
		}
	}

	public static void w(String TAG, String content) {
		if (isLog) {
			android.util.Log.w(TAG, content);
		}
	}

	public static void w(String TAG, String content, Throwable tr) {
		if (isLog) {
			android.util.Log.w(TAG, content, tr);
		}
	}
}
