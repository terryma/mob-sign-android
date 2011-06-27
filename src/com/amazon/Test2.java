package com.amazon;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.http.AndroidHttpClient;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Test2 extends Activity implements View.OnClickListener {

	LinearLayout view;
	Button allow;
	Button deny;
	BReceiver breceiver;
	Tuple currentTuple = null;
	Queue<Tuple> queue = new LinkedList<Tuple>();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		view = (LinearLayout) findViewById(R.id.my_relative_layout_id);
		allow = (Button) findViewById(R.id.allow);
		allow.setOnClickListener(this);
		deny = (Button) findViewById(R.id.deny);
		deny.setOnClickListener(this);
		// Initialize BroadCastReceiver and register
		breceiver = new BReceiver(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		IntentFilter filter = new IntentFilter("com.amazon.REQUEST");
		getApplication().registerReceiver(breceiver, filter);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		getApplication().unregisterReceiver(breceiver);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("ApprovalApp", "");
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().get("PermRequest") != null) {
			updateForIntent(getIntent());
		}
		Intent i = new Intent(this, Test.class);
		this.startService(i);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	/**
	 * Update the Intent
	 * 
	 * @param i
	 */
	public void updateForIntent(Intent i) {
		if (i != null) {
			queue.addAll(((FinalOutput) i.getExtras().get("PermRequest")).auth_requests);
		}
		if (currentTuple == null && queue.size() > 0) {
			currentTuple = queue.poll();
			TextView text = (TextView) findViewById(R.id.request_title);
			text.setText(currentTuple.site + " - " + currentTuple.uid);
			text.invalidate();
		} else {
			TextView text = (TextView) findViewById(R.id.request_title);
			text.setText("");
			text.invalidate();			
		}
	}

	@Override
	public void onClick(final View v) {

		boolean allowed = false;
		if (v == allow) {
			allowed = true;
		} else if (v == deny) {
			allowed = false;
		}

		final boolean Allowed = allowed;

		new Thread(new Runnable() {
			public void run() {

				StringBuffer queryString = new StringBuffer(
						"http://184.72.102.84/mobile-auth?deviceId=12345&uid="
								+ currentTuple.uid + "&site="
								+ currentTuple.site + "&authed=");
				queryString.append(Allowed);
				HttpClient client = AndroidHttpClient.newInstance("fuckme!");
				HttpGet get = new HttpGet(queryString.toString());
				// Make the URL and post it
				HttpResponse response = null;
				try {
					response = client.execute(get);
					response.getEntity().consumeContent();
					((AndroidHttpClient)client).close();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				view.post(new Runnable() {
					@Override
					public void run() {
						// Remove the current tuple
						currentTuple = null;
						updateForIntent(null);						
					}
				});
			}
		}).start();

	}

}
