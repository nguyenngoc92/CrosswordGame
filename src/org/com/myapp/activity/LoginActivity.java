package org.com.myapp.activity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.com.myapp.AppInitial;
import org.com.myapp.inet.HttpConnection;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends ActionBarActivity {

	private EditText edtEmail;
	private EditText edtPassword;

	private Button btnLogin;
	private TextView tvForgotPassword;
	private TextView tvErrorLogin;

	// json object user
	private static final String TAG_USERNAME = "username";
	private static final String TAG_SCORE = "score";
	private static final String TAG_RANK = "rank";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		this.init();
	}

	private void init() {
		edtEmail = (EditText) findViewById(R.id.edtEmail);
		edtPassword = (EditText) findViewById(R.id.edtPassword);

		btnLogin = (Button) findViewById(R.id.btnLogin);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		tvErrorLogin = (TextView) findViewById(R.id.tvErrorLogin);

		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (HttpConnection.checkNetWorkState(getApplicationContext())) {

					sendPostRequestLogin(edtEmail.getText().toString(),
							edtPassword.getText().toString());

				} else {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Not connection internet !", Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		});

		tvForgotPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void sendPostRequestLogin(String email, String password) {

		class SendLoginPostReqAsyncTask extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {

				String paramUsername = params[0];
				String paramPassword = params[1];

				System.out.println("*** doInBackground ** paramUsername "
						+ paramUsername + " paramPassword :" + paramPassword);

				HttpClient httpClient = HttpConnection.getInstance()
						.getHttpClient();

				HttpPost httpPost = new HttpPost(AppInitial.loginUrl);

				BasicNameValuePair usernameBasicNameValuePair = new BasicNameValuePair(
						"email", paramUsername);
				BasicNameValuePair passwordBasicNameValuePair = new BasicNameValuePair(
						"password", paramPassword);

				List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>();

				nameValuePairList.add(usernameBasicNameValuePair);
				nameValuePairList.add(passwordBasicNameValuePair);

				try {

					UrlEncodedFormEntity urlEncodedFormEntity = new UrlEncodedFormEntity(
							nameValuePairList);

					httpPost.setEntity(urlEncodedFormEntity);

					try {

						HttpResponse httpResponse = httpClient
								.execute(httpPost);

						return getResponse(httpResponse);
					} catch (ClientProtocolException cpe) {

						System.out.println("Firstption caz of HttpResponese :"
								+ cpe);
						cpe.printStackTrace();
					} catch (IOException ioe) {
						System.out.println("Secondption caz of HttpResponse :"
								+ ioe);
						ioe.printStackTrace();
					}

				} catch (UnsupportedEncodingException uee) {

					System.out
							.println("Anption given because of UrlEncodedFormEntity argument :"
									+ uee);
					uee.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				super.onPostExecute(result);

				if (!result.equals("SUCCESS")) {
					tvErrorLogin.setText("Wrong username or password !");
				} else {

					sendRequestGetUserInfor();
				}
				System.out.println("Result: " + result);

			}

		}

		SendLoginPostReqAsyncTask sendPostReqAsyncTask = new SendLoginPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password);

	}

	private void sendRequestGetUserInfor() {

		class SendGetUserInforReqAsyncTask extends
				AsyncTask<Void, Void, String> {

			@Override
			protected String doInBackground(Void... params) {
				HttpClient httpClient = HttpConnection.getInstance()
						.getHttpClient();

				HttpGet httpGet = new HttpGet(AppInitial.userInforUrl);

				try {

					HttpResponse httpResponse = httpClient.execute(httpGet);

					return getResponse(httpResponse);
				} catch (ClientProtocolException cpe) {

					System.out.println("Firstption caz of HttpResponese :"
							+ cpe);
					cpe.printStackTrace();
				} catch (IOException ioe) {
					System.out.println("Secondption caz of HttpResponse :"
							+ ioe);
					ioe.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				super.onPostExecute(result);

				if (result != null) {
					try {
						JSONObject reader = new JSONObject(result);

						String username = reader.getString(TAG_USERNAME);
						String score = reader.getString(TAG_SCORE);
						String rank = reader.getString(TAG_RANK);

						// start main activity
						Intent intent = new Intent(getApplication()
								.getApplicationContext(), MainActivity.class);
						intent.putExtra(AppInitial.USER_NAME, username);
						intent.putExtra(AppInitial.SCORE, score);
						intent.putExtra(AppInitial.RANK, rank);
						startActivity(intent);

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		}

		SendGetUserInforReqAsyncTask sendGetUserInforReqAsyncTask = new SendGetUserInforReqAsyncTask();
		sendGetUserInforReqAsyncTask.execute(null, null);
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
