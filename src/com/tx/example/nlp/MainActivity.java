package com.tx.example.nlp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

public class MainActivity extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getListView().setAdapter(
				new ArrayAdapter<String>(this,
						android.R.layout.simple_list_item_1, new String[] {
								"location", "geocode", "rgeocode" }));
		getListView().setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				switch (position) {
				case 0:
					startActivity(new Intent(MainActivity.this,
							TestLocationActivity.class));
					break;
				case 1:
					startActivity(new Intent(MainActivity.this,
							TestGeocodeActivity.class));
					break;
				case 2:
					startActivity(new Intent(MainActivity.this,
							TestRgeocodeActivity.class));
					break;

				default:
					break;
				}
			}
		});
	}
}
