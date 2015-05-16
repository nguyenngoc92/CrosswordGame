package org.com.myapp.activity;

import java.net.SocketTimeoutException;
import java.util.LinkedList;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.ProgressDialog;
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
	private TextView tvCreateAccount;

	private HttpConnection httpConnection = HttpConnection.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sign_in_screen);

		this.init();
	}

	private void init() {
		edtEmail = (EditText) findViewById(R.id.etUserName);
		edtPassword = (EditText) findViewById(R.id.etPass);

		btnLogin = (Button) findViewById(R.id.btnSingIn);
		tvForgotPassword = (TextView) findViewById(R.id.tvForgotPassword);
		tvErrorLogin = (TextView) findViewById(R.id.tvErrorLogin);

		btnLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (httpConnection.checkNetWorkState(getApplicationContext())) {

					String str = this.validateForm(edtEmail.getText()
							.toString(), edtPassword.getText().toString());
					if (str != null) {
						tvErrorLogin.setText(str);
					} else {
						sendPostRequestLogin(edtEmail.getText().toString(),
								edtPassword.getText().toString());
					}

				} else {

					Toast toast = Toast.makeText(getApplicationContext(),
							"Not connection internet !", Toast.LENGTH_SHORT);
					toast.show();
				}

			}

			private String validateForm(String email, String password) {

				if (email == null || email.length() == 0) {
					return "Email cannot blank !";
				} else if (email != null
						&& !android.util.Patterns.EMAIL_ADDRESS.matcher(email)
								.matches()) {

					return "Invalid email !";

				} else if (password == null || password.length() == 0) {

					return "Password cannot blank !";
				}

				return null;

			}
		});

		tvCreateAccount = (TextView) findViewById(R.id.tvCreateAccount);
		tvCreateAccount.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(getApplicationContext(),
						RegisterActivity.class);
				startActivity(intent);
			}
		});

		tvForgotPassword.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});
	}

	private void sendPostRequestLogin(final String email, final String password) {

		class SendLoginPostReqAsyncTask extends AsyncTask<String, Void, String> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(LoginActivity.this);
				progressDialog.setMessage("Authenticating user...");
				progressDialog.show();

				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... params) {
				String email = params[0];
				String password = params[1];

				try {

					List<HttpMessageConverter<?>> messageConverters = new LinkedList<HttpMessageConverter<?>>();

					messageConverters.add(new FormHttpMessageConverter());
					messageConverters.add(new StringHttpMessageConverter());

					MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
					map.add("email", email);
					map.add("password", password);

					RestTemplate restTemplate = HttpConnection.getInstance()
							.getRestTemplate();
					restTemplate.setMessageConverters(messageConverters);
					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders
							.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

					org.springframework.http.HttpEntity<MultiValueMap<String, String>> entity = new org.springframework.http.HttpEntity<MultiValueMap<String, String>>(
							map, requestHeaders);

					ResponseEntity<String> result = restTemplate.exchange(
							AppConfig.loginUrl, HttpMethod.POST, entity,
							String.class);

					HttpHeaders respHeaders = result.getHeaders();
					String cookies = respHeaders.getFirst("Set-Cookie");

					// System.out.println(cookies);
					return cookies;

				} catch (HttpClientErrorException ex) {

				} catch (HttpServerErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				super.onPostExecute(result);
				progressDialog.dismiss();

				if (result.contains("JSESSIONID")) {
					RestTemplate restTemplate = httpConnection
							.createRestTemplate(email, password,
									AppConfig.host, AppConfig.port);
					httpConnection.setRestTemplate(restTemplate);
					Intent intent = new Intent(getApplication()
							.getApplicationContext(), MainActivity.class);
					startActivity(intent);

				} else {
					tvErrorLogin.setText("Wrong username or password !");
				}

			}

		}

		SendLoginPostReqAsyncTask sendPostReqAsyncTask = new SendLoginPostReqAsyncTask();
		sendPostReqAsyncTask.execute(email, password);
	}

}
