package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.MatchData;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends ActionBarActivity {

	private HttpConnection httpConnection = HttpConnection.getInstance();
	ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		this.init();

	}

	private void init() {
		Button btnPlay = (Button) findViewById(R.id.btnPlay);

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				if (httpConnection.checkNetWorkState(getApplicationContext())) {
					sendRequestGetMatch();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Not connection internet !", Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		});

		Button btnRank = (Button) findViewById(R.id.btnRank);

		btnRank.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {

				Intent intent = new Intent(getApplicationContext(),
						ListRankActivity.class);
				// intent.putExtra(AppConfig.USER_DATA_LIST, RESULT_O)
				startActivity(intent);

			}
		});
	}

	private void sendRequestGetMatch() {

		class SendGetMatchGetReqAsyncTask extends
				AsyncTask<Void, Void, MatchData> {

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Waiting data...");
				progressDialog.show();
			}

			@Override
			protected MatchData doInBackground(Void... arg0) {

				try {

					RestTemplate restTemplate = httpConnection
							.getRestTemplate();
					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					MatchData match = restTemplate.getForObject(
							AppConfig.getMatchUrl, MatchData.class);

					return match;
				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;

			}

			@Override
			protected void onPostExecute(MatchData result) {

				super.onPostExecute(result);
				progressDialog.dismiss();

				if (result != null) {
					Intent intent = new Intent(getApplicationContext(),
							GameActivity.class);

					if (result.getItems().size() != 0) {
						intent.putExtra("match", result);
						startActivity(intent);
					} else {

						Toast t = Toast.makeText(MainActivity.this, "No Data",
								Toast.LENGTH_SHORT);
						t.show();
					}

				}
			}

		}

		SendGetMatchGetReqAsyncTask sendGetMatchGetReqAsyncTask = new SendGetMatchGetReqAsyncTask();
		sendGetMatchGetReqAsyncTask.execute();

	}

	private void sendRequestGetListUserRank(int row, int limit) {

		class SendRequestGetListUserRank extends
				AsyncTask<Integer, Void, List<UserData>> {

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Waiting data...");
				progressDialog.show();
				super.onPreExecute();
			}

			@Override
			protected List<UserData> doInBackground(Integer... params) {

				int row = params[0];
				int limit = params[1];

				RestTemplate restTemplate = httpConnection.getRestTemplate();

				try {

					String url = AppConfig.getUserInforListUrl;
					String data = row + "/" + limit;

					ResponseEntity<UserData[]> result = restTemplate
							.getForEntity(url + data, UserData[].class);

					if (result.getStatusCode() == HttpStatus.OK) {

						List<UserData> userDatas = new ArrayList<UserData>();

						userDatas.addAll(Arrays.asList(result.getBody()));

						return userDatas;

					}
				} catch (HttpClientErrorException e) {

					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(List<UserData> result) {

				if (result != null) {

					Intent intent = new Intent(getApplicationContext(),
							ListRankActivity.class);
					// intent.putExtra(AppConfig.USER_DATA_LIST, RESULT_O)
					startActivity(intent);

				}
				super.onPostExecute(result);
				progressDialog.dismiss();

			}

		}

		SendRequestGetListUserRank request = new SendRequestGetListUserRank();
		request.execute(row, limit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
