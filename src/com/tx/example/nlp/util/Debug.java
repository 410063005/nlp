package com.tx.example.nlp.util;

import android.util.Log;

public class Debug {

	public static void i(String tag, String msg) {
//		Log.i(tag, msg);
	}

	public static void i(String tag, String msg, boolean showThreadName) {
//		if (showThreadName) {
//			Log.i(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
//		} else {
//			Log.i(tag, msg);
//		}
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}
}
