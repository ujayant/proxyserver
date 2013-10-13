package net.jayantupadhyaya.proxyserver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class URLReceiver extends BroadcastReceiver {
	private static final String TAG = "URLReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i(TAG, "URL Received");
		intent.setClass(context, NetworkService.class);
		context.startService(intent);
	}
}
