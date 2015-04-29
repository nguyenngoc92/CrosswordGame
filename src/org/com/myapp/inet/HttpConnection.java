package org.com.myapp.inet;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class HttpConnection {

	private static HttpConnection instance = new HttpConnection();
	private RestTemplate restTemplate;

	private HttpConnection() {
		restTemplate = new RestTemplate();
	}

	public static HttpConnection getInstance() {
		return instance;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate createRestTemplate(String username, String password,
			String host, int port) {
		ClientHttpRequestFactory factory = this.createSecureTransport(username,
				password, host, port);

		return new RestTemplate(factory);
	}

	public Boolean checkNetWorkState(Context context) {

		ConnectivityManager connMgr = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

		boolean isConnected = networkInfo != null
				&& networkInfo.isConnectedOrConnecting();

		return isConnected;
	}

	private ClientHttpRequestFactory createSecureTransport(String username,
			String password, String host, int port) {
		DefaultHttpClient client = new DefaultHttpClient();
		UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
				username, password);
		client.getCredentialsProvider().setCredentials(
				new AuthScope(host, port), credentials);

		return new HttpComponentsClientHttpRequestFactory(client);
	}

}
