package com.amazon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Home Activity
 * 
 * @author parthp
 */
public class Home extends Activity implements OnClickListener {
	TextView service_on_message;
	CheckBox checkbox;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		checkbox = (CheckBox) findViewById(R.id.service_control);
		checkbox.setOnClickListener(this);
		service_on_message = (TextView) findViewById(R.id.service_on_message);
		service_on_message.setVisibility(View.INVISIBLE);
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		if (v == checkbox) {
			if (checkbox.isChecked()) {
				startService();
				service_on_message.setVisibility(View.VISIBLE);
			} else {
				stopService();
				service_on_message.setVisibility(View.INVISIBLE);
			}
		}
	}

	private void startService() {
		/* Intent i = new Intent(this, RequestService.class); */
		Intent i = new Intent("com.amazon.REQUEST_SERVICE");
		this.startService(i);
	}

	private void stopService() {
		Intent i = new Intent("com.amazon.REQUEST_SERVICE");
		i.setAction("com.amazon.REQUEST_SERVICE");
		this.stopService(i);
	}

}
