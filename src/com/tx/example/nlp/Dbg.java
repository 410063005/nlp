package com.tx.example.nlp;

import android.util.Log;

public class Dbg {

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static void i(String tag, String msg, boolean showThreadName) {
		if (showThreadName) {
			Log.i(tag, "[" + Thread.currentThread().getName() + "]:" + msg);
		} else {
			Log.i(tag, msg);
		}
	}
}
