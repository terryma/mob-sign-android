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

/**
 * Activity for Popping out the Auth Requests on Screen
 * 
 * @author parthp
 * 
 */
public class AuthRequestsActivity extends Activity implements
		View.OnClickListener {

	LinearLayout view;
	Button allow;
	Button deny;

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
	}

	@Override
	protected void onStart() {
		super.onStart();
		Log.d("ApprovalApp", "");
		if (getIntent() != null && getIntent().getExtras() != null
				&& getIntent().getExtras().get("PermRequest") != null) {
			updateForIntent(getIntent());
		}
	}

	/**
	 * For Every new intent from the Service, add the requests to the queue.
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		this.updateForIntent(intent);
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
		}

		if (currentTuple != null) {
			TextView text = (TextView) findViewById(R.id.request_title);
			text.setText(currentTuple.site + " - " + currentTuple.uid);
			text.invalidate();
		} else {
			finish();
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

		AllowOrDeny(allowed);

	}

	public void AllowOrDeny(final Boolean Allowed) {

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
					((AndroidHttpClient) client).close();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
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
