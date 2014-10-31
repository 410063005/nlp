package com.tx.example.nlp.util;


import android.annotation.SuppressLint;
import android.content.Context;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

public class LegacyWrapper {

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public static boolean isNetworkLocationProviderEnabled(Context context) {
		if (Build.VERSION.SDK_INT >= 19) {
			int mode = Settings.Secure.getInt(context.getContentResolver(),
					Settings.Secure.LOCATION_MODE,
					Settings.Secure.LOCATION_MODE_OFF);
			return mode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING
					|| mode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY;
		} else {
			return Settings.Secure.isLocationProviderEnabled(
					context.getContentResolver(),
					LocationManager.NETWORK_PROVIDER);
		}
	}

	@SuppressLint("InlinedApi")
	@SuppressWarnings("deprecation")
	public static void setNetworkLocationProviderEnabled(Context context,
			boolean enabled) {
		if (enabled == isNetworkLocationProviderEnabled(context)) {
			return;
		}
		if (Build.VERSION.SDK_INT >= 19) {
			try {
				Settings.Secure.putInt(context.getContentResolver(),
						Settings.Secure.LOCATION_MODE,
						Settings.Secure.LOCATION_MODE_HIGH_ACCURACY);
			} catch (Exception e) {
				Debug.e("LegacyWrapper", "setNetworkLocationProviderEnabled(api 19): "
						+ Log.getStackTraceString(e));
			}
			return;
		}

		try {
			Settings.Secure.setLocationProviderEnabled(
					context.getContentResolver(),
					LocationManager.NETWORK_PROVIDER, enabled);
		} catch (Exception e) {
			Debug.e("LegacyWrapper", "setNetworkLocationProviderEnabled: "
					+ Log.getStackTraceString(e));
		}
	}
}
