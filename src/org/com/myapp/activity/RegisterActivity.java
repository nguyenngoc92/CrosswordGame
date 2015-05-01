package org.com.myapp.activity;

import java.util.LinkedList;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.RegisterForm;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
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

public class RegisterActivity extends ActionBarActivity {

	private HttpConnection httpConnection = HttpConnection.getInstance();

	private EditText edtUsername;
	private EditText edtEmail;
	private EditText edtPassword;
	private EditText edtRePassword;
	private Button btnSignUp;
	private TextView tvRegisterError;
	private TextView tvLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

		this.initial();
	}

	private void initial() {

		this.edtUsername = (EditText) findViewById(R.id.etUserName);
		this.edtEmail = (EditText) findViewById(R.id.etEmail);
		this.edtPassword = (EditText) findViewById(R.id.etPass);
		this.edtRePassword = (EditText) findViewById(R.id.etRe_Pass);
		this.tvLogin = (TextView) findViewById(R.id.tvLogin);
		this.tvRegisterError = (TextView) findViewById(R.id.tvRegisterError);

		tvLogin.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(getApplicationContext(),
						LoginActivity.class);
				startActivity(intent);

			}
		});
		this.btnSignUp = (Button) findViewById(R.id.btnSingUp);
		btnSignUp.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String username = edtUsername.getText().toString();
				String email = edtEmail.getText().toString();
				String password = edtPassword.getText().toString();
				String re_password = edtRePassword.getText().toString();

				String messageValidate = this.validateForm(username, email,
						password, re_password);
				if (messageValidate != null) {

					tvRegisterError.setText(messageValidate);

				} else {

					if (httpConnection
							.checkNetWorkState(getApplicationContext())) {

						sendPostRequestRegister(username, email, password,
								re_password);

					} else {
						Toast toast = Toast
								.makeText(getApplicationContext(),
										"Not connection internet !",
										Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			}

			private String validateForm(String username, String email,
					String password, String rePassword) {

				if (username == null || username.length() == 0
						|| username.length() < 5) {
					if (username == null || username.length() == 0)
						return "Username cannot blank!";
					else if (username.length() < 5)
						return "Username atleast 5 character !";
				} else if (email == null || email.length() == 0) {
					return "Email cannot blank !";
				} else if (!validateEmail(email)) {

					return "Invalid email !";
				} else if (password == null || password.length() == 0) {
					return "Password cannot balnk";
				} else if (password.length() < 8) {
					return "Password atleast 8 character !";
				} else if (rePassword == null || rePassword.length() == 0) {
					return "Re-password cannot balnk";
				} else if (!password.equals(rePassword)) {
					return "Password not match";
				}

				return null;
			}

			private boolean validateEmail(String email) {

				return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
						.matches();
			}
		});

	}

	private void sendPostRequestRegister(String username, String email,
			String password, String re_password) {

		class SendRegisterPostReqAsyncTask extends
				AsyncTask<String, Void, String> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(RegisterActivity.this);
				progressDialog.setMessage("Create user...");
				progressDialog.show();

				super.onPreExecute();
			}

			@Override
			protected String doInBackground(String... params) {

				String username = params[0];
				String email = params[1];
				String password = params[2];
				String re_password = params[3];

				List<HttpMessageConverter<?>> messageConverters = new LinkedList<HttpMessageConverter<?>>();

				messageConverters.add(new FormHttpMessageConverter());
				messageConverters.add(new StringHttpMessageConverter());
				messageConverters
						.add(new MappingJackson2HttpMessageConverter());

				try {
					RestTemplate restTemplate = httpConnection
							.getRestTemplate();
					restTemplate.setMessageConverters(messageConverters);

					RegisterForm registerForm = new RegisterForm(username,
							email, password, re_password);

					String result = restTemplate.postForObject(
							AppConfig.registerUrl, registerForm, String.class);

					System.out.println(result);
					return result;

				} catch (HttpClientErrorException e) {
					e.printStackTrace();

				}

				return null;
			}

			@Override
			protected void onPostExecute(String result) {

				super.onPostExecute(result);
				progressDialog.dismiss();
				if (result != null) {

					if (result == "SUCCESS") {

						Intent intent = new Intent(getApplicationContext(),MainActivity.class);
						startActivity(intent);
					} else if (result == "FAIL") {
						tvRegisterError.setText("Register fail !!!");
					}
				} else {

					tvRegisterError.setText("Email has been use !");
				}

			}

		}

		SendRegisterPostReqAsyncTask request = new SendRegisterPostReqAsyncTask();
		request.execute(username, email, password, re_password);

	}

}
