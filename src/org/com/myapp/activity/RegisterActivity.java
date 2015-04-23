package org.com.myapp.activity;

import java.util.LinkedList;
import java.util.List;

import org.com.myapp.inet.HttpConnection;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import android.app.ProgressDialog;
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
	private Button btnRegister;
	private TextView tvRegisterError;
	private TextView tvLogin;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);

		this.initial();
	}

	private void initial() {

		this.edtUsername = (EditText) findViewById(R.id.edtRegisterUserName);
		this.edtEmail = (EditText) findViewById(R.id.edtRegisterEmail);
		this.edtPassword = (EditText) findViewById(R.id.edtRegisterPassword);
		this.edtRePassword = (EditText) findViewById(R.id.edtRegisterRe_password);
		this.tvLogin = (TextView) findViewById(R.id.tvLogin);
		this.tvRegisterError = (TextView) findViewById(R.id.tvRegisterError);

		this.btnRegister = (Button) findViewById(R.id.btnRegister);
		btnRegister.setOnClickListener(new View.OnClickListener() {

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

						this.sendPostRequestRegister(username, email,password,re_password);
						
					} else {
						Toast toast = Toast
								.makeText(getApplicationContext(),
										"Not connection internet !",
										Toast.LENGTH_SHORT);
						toast.show();
					}

				}
			}

			private void sendPostRequestRegister(String username, String email,
					String password, String re_password) {
				
				
				class SendRegisterPostReqAsyncTask extends AsyncTask<String, Void, String>{

					ProgressDialog progressDialog;
					
					@Override
					protected void onPreExecute() {
						
						progressDialog = new ProgressDialog(getApplicationContext());
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
						messageConverters.add(new MappingJackson2HttpMessageConverter());

						MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
						
						
						
						
						
						
						
						
						return null;
					}
					

					@Override
					protected void onPostExecute(String result) {
						// TODO Auto-generated method stub
						super.onPostExecute(result);
						progressDialog.dismiss();
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

}
