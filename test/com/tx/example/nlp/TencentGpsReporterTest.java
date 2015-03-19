package com.tx.example.nlp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.location.Location;
import android.util.Log;

public class TencentGpsReporterTest extends BaseTestCase {

	private TencentGpsReporter mGpsReporter;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mGpsReporter = new TencentGpsReporter(getContext());
	}

	public void testOnLocationChanged() throws InterruptedException {
		Location location = createLocation();
		mGpsReporter.onLocationChanged(location);

		TimeUnit.SECONDS.sleep(5);
		assertTrue(mGpsReporter.myCache().size() > 0);

		Log.i("TencentGpsReporterTest", getFileContent(mGpsReporter.myCache().getFile()));

		mGpsReporter.myCache().delete();
		assertFalse(mGpsReporter.myCache().getFile().exists());
	}

	private static String getFileContent(File f) {
		BufferedReader reader;
		StringBuilder sb = new StringBuilder();
		try {
			reader = new BufferedReader(new FileReader(f));
			String line;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}
			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return sb.toString();
	}
}
