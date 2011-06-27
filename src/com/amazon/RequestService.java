package com.amazon;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.IBinder;
import android.util.Log;

import com.google.gson.Gson;

/**
 * Service that listens to the Auth Requests.
 * 
 * @author parthp
 * 
 */
public class RequestService extends Service {

	Timer timer = new Timer();

	class MyTimerTask extends TimerTask {

		Service service;

		public MyTimerTask(Service service) {
			this.service = service;
		}

		@Override
		public void run() {
			HttpClient client = AndroidHttpClient.newInstance("fuckme!");
			HttpGet post = new HttpGet(
					"http://184.72.102.84/pull-device?deviceId=12345");
			HttpResponse response = null;
			InputStream is = null;

			String finalOutput = null;
			try {
				response = client.execute(post);
				is = response.getEntity().getContent();
				BufferedInputStream bis = new BufferedInputStream(is);
				StringBuffer buffer = new StringBuffer(1024);
				int byteRead = 0;
				byte[] bytes = new byte[1024];
				while ((byteRead = bis.read(bytes)) >= 0) {
					buffer.append(new String(bytes, 0, byteRead));
				}
				finalOutput = buffer.toString();

				Log.d("Test", buffer.toString());
				
				response.getEntity().consumeContent();
				is.close();
				((AndroidHttpClient) client).close();
			} catch (Exception e) {
				Log.d("Exception", e.toString());
			}

			// Parse the finalOutput
			Gson gson = new Gson();
			FinalOutput output = gson.fromJson(finalOutput, FinalOutput.class);
			if (output.auth_requests.size() > 0 && "success".equals(output.msg)) {
				sendIntent(output);
			}
		}

		public void sendIntent(FinalOutput output) {
			try {
				Intent i1 = new Intent("com.amazon.RUNPERMS").putExtra(
						"PermRequest", output);
				i1.setComponent(new ComponentName("com.amazon",
						"com.amazon.AuthRequestsActivity"));
				i1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				service.startActivity(i1);
			} catch (Exception e) {
				Log.v("Exception", e.toString());
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		timer.schedule(new MyTimerTask(this), 1000, 3000);
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void onDestroy() {
		timer.cancel();
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	////////// DEAD Code for now until we need to add SSL
	public HttpClient getNewHttpClient() {
		try {
			KeyStore trustStore = KeyStore.getInstance(KeyStore
					.getDefaultType());
			trustStore.load(null, null);

			SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
			sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

			HttpParams params = new BasicHttpParams();
			HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory
					.getSocketFactory(), 80));
			registry.register(new Scheme("https", sf, 443));

			ClientConnectionManager ccm = new ThreadSafeClientConnManager(
					params, registry);

			return new DefaultHttpClient(ccm, params);
		} catch (Exception e) {
			return new DefaultHttpClient();
		}
	}

}