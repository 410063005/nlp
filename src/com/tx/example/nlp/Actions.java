package com.tx.example.nlp;

/**
 * action 常量.
 *
 * <p>
 * action 值因厂商、系统版本等因素不尽相同, 安装 NLP 前一定要确认我们的 action 定义跟系统当前的 action 一致, 否则
 * framework location service 可能无法绑定到我们的 service
 *
 */
public class Actions {

	/**
	 * Android 2.3, Android 4.0, Android 4.1 上通常使用的 action
	 */
	public static class V {
		public static final String GEOCODE = "com.google.android.location.GeocodeProvider";
		public static final String LOCATION = "com.google.android.location.NetworkLocationProvider";

		public static final String GEOCODE2 = "com.android.location.service.GeocodeProvider";
		public static final String LOCATION2 = "com.android.location.service.NetworkLocationProvider";

	}

	/**
	 * Android 4.2 上通常使用的 action
	 */
	public static class V2 {
		public static final String GEOCODE = "com.google.android.location.v2.GeocodeProvider";
		public static final String LOCATION = "com.google.android.location.v2.NetworkLocationProvider";
	}

	// TODO 其他的 action, 如小米系统
}
