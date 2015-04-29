package org.com.myapp.activity;

import java.util.HashMap;
import java.util.Map;

import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class MyRankActivity extends ActionBarActivity {

	private HttpConnection httpConnection = HttpConnection.getInstance();

	private int matchId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myrank);
		matchId = getIntent().getIntExtra("MATCHID", 0);
		this.init();
	}

	private void init() {
		if (httpConnection.checkNetWorkState(MyRankActivity.this)) {

			if (matchId != 0) {
				sendRequestGetUserRankByMatch(matchId);
			} else {
				System.out.println(matchId);
			}

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Not connection internet !", Toast.LENGTH_SHORT);
			toast.show();

		}
	}

	private void sendRequestGetUserRankByMatch(int matchId) {

		class SendRequestGetUserRankByMatch extends
				AsyncTask<Integer, Void, UserData> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(MyRankActivity.this);
				progressDialog.setMessage("Loading data ...");
				progressDialog.show();

				super.onPreExecute();
			}

			@Override
			protected UserData doInBackground(Integer... params) {

				int matchId = params[0];
				try {
					RestTemplate restTemplate = httpConnection
							.getRestTemplate();

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("id", matchId + "");

					ResponseEntity<UserData> entity = restTemplate
							.getForEntity(AppConfig.getRankUserByMatchUrl,
									UserData.class, paramsMap);

					if (entity.getStatusCode() == HttpStatus.OK) {

						return entity.getBody();
					}
				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(UserData result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);

				if (result != null) {
					System.out.println(result.getUsername());
				}
				progressDialog.dismiss();
			}

		}
		SendRequestGetUserRankByMatch request = new SendRequestGetUserRankByMatch();
		request.execute(matchId);

	}
}
