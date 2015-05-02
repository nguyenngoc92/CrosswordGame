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
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MyRankActivity extends ActionBarActivity {

	private HttpConnection httpConnection = HttpConnection.getInstance();

	private int id;
	private String flag;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_myrank);
		id = getIntent().getIntExtra("ID", 0);
		flag = getIntent().getStringExtra(AppConfig.FLAG);
		System.out.println(flag + " " + id);
		this.init();
	}

	private void init() {
		if (httpConnection.checkNetWorkState(MyRankActivity.this)) {

			if (flag.equalsIgnoreCase(AppConfig.FLAG_COMPETITION)) {
				if (id != 0) {
					sendRequestGetUserRankByCompetition(id);
				} else {
					System.out.println(id);
				}
			}

		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Not connection internet !", Toast.LENGTH_SHORT);
			toast.show();

		}
	}

	private void sendRequestGetUserRankByCompetition(final int id) {

		class SendRequestGetUserRankByCompetition extends
				AsyncTask<Integer, Void, ResponseEntity<UserData>> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(MyRankActivity.this);
				progressDialog.setMessage("Loading data ...");
				progressDialog.show();

				super.onPreExecute();
			}

			@Override
			protected ResponseEntity<UserData> doInBackground(Integer... params) {

				int competitionId = params[0];
				try {
					RestTemplate restTemplate = httpConnection
							.getRestTemplate();

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("id", competitionId + "");

					ResponseEntity<UserData> entity = restTemplate
							.getForEntity(
									AppConfig.getRankUserByCompetitionUrl,
									UserData.class, paramsMap);

					return entity;
				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(ResponseEntity<UserData> result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				if (result.getStatusCode() == HttpStatus.OK) {

					UserData userData = result.getBody();
					if (userData != null
							&& userData.getUsername().equalsIgnoreCase(
									"NOT_HAVE_SCORE")) {
						System.out.println(userData.getUsername());
						TextView tvUsername = (TextView) findViewById(R.id.tvUsername);
						TextView tvScore = (TextView) findViewById(R.id.tvScore);
						TextView tvRnank = (TextView) findViewById(R.id.tvRank);

						tvUsername.setText(userData.getUsername());
						tvScore.setText(userData.getScore() + "");
						tvRnank.setText(userData.getRank() + "");

						Button btn = (Button) findViewById(R.id.btnSeeTopRank);
						btn.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								Intent intent = new Intent(MyRankActivity.this,
										ListRankActivity.class);
								intent.putExtra("ID", id);
								intent.putExtra(AppConfig.FLAG, flag);
								startActivity(intent);

							}
						});
					} else {
						Toast t = Toast.makeText(MyRankActivity.this,
								"You need to finish this match !",
								Toast.LENGTH_SHORT);
						t.show();
					}
				}

				progressDialog.dismiss();
			}

		}
		SendRequestGetUserRankByCompetition request = new SendRequestGetUserRankByCompetition();
		request.execute(id);

	}
}
