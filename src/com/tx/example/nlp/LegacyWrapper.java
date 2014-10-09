package com.tx.example.nlp;

import com.tx.example.nlp.util.Debug;

import android.content.Context;
import android.location.LocationManager;
import android.provider.Settings;
import android.util.Log;

public class LegacyWrapper {

	@SuppressWarnings("deprecation")
	public static boolean isNetworkLocationProviderEnabled(Context context) {
		return Settings.Secure.isLocationProviderEnabled(
				context.getContentResolver(), LocationManager.NETWORK_PROVIDER);
	}

	@SuppressWarnings("deprecation")
	public static void setNetworkLocationProviderEnabled(Context context,
			boolean enabled) {
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
