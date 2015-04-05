package org.com.myapp.inet;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpConnection {

	private HttpClient httpClient;

	private static HttpConnection instance = new HttpConnection();

	private HttpConnection() {
		httpClient = new DefaultHttpClient();
	}

	public static HttpConnection getInstance() {
		return instance;
	}

	public HttpClient getHttpClient() {
		return httpClient;
	}

	public static Boolean checkNetWorkState(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		boolean isConnected = networkInfo != null
				&& networkInfo.isConnectedOrConnecting();

		return isConnected;
	}

}
