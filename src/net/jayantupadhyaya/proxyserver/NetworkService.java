package net.jayantupadhyaya.proxyserver;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class NetworkService extends IntentService {
	private static final String TAG = "NetworkService";
	
	public NetworkService() {
		super("NetworkService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		HttpURLConnection urlConnection = null;
		String response = null;
		URL receivedLink = null;
		
		if (isNetworkAvailable()) {
			System.setProperty("http.maxRedirects", "5");
			try {
				receivedLink = new URL(intent.getStringExtra("SEND_URL"));
			} catch (MalformedURLException e1) {
				e1.printStackTrace();
			}
			try {
				urlConnection = (HttpURLConnection) receivedLink.openConnection();
				HttpURLConnection.setFollowRedirects(true);
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				response = inputStreamtoString(in);
			} catch (IOException e) {
				e.printStackTrace();
			}
			if (response != null) {
				Intent responseIntent = new Intent();
				responseIntent.setAction("net.jayantupadhyaya.proxyclient.client");
				responseIntent.putExtra("RESPONSE", response);
				sendBroadcast(responseIntent);
				Log.i(TAG, "Response Sent to Client");
			}
		}
	}

	private String inputStreamtoString(InputStream in) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		String line;
		try {
			br = new BufferedReader(new InputStreamReader(in));
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return sb.toString();
	}

	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = cm.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}
}
