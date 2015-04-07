package org.com.myapp.inet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
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

	public Boolean checkNetWorkState(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		boolean isConnected = networkInfo != null
				&& networkInfo.isConnectedOrConnecting();

		return isConnected;
	}

	public String getResponse(HttpResponse httpResponse) {

		InputStream inputStream = null;
		try {
			inputStream = httpResponse.getEntity().getContent();

		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

		BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

		StringBuilder stringBuilder = new StringBuilder();

		String bufferedStrChunk = null;

		try {
			while ((bufferedStrChunk = bufferedReader.readLine()) != null) {
				stringBuilder.append(bufferedStrChunk);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return stringBuilder.toString();

	}

}
