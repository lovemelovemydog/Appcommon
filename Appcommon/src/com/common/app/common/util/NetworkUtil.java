package com.common.app.common.util;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtil {
	public static String TAG = "NetworkUtil";
	public static boolean checkNetwork(final Application app) {
		ConnectivityManager manager = (ConnectivityManager) app.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();
		if(networkInfo != null) {
			return networkInfo.isConnected();
		} else {
			
		}

		return false;
	}

}
