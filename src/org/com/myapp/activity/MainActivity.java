package org.com.myapp.activity;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.com.myapp.AppInitial;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.Item;
import org.com.myapp.model.Match;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

	private Button btnPlay;

	private HttpConnection httpConnection = HttpConnection.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		/*
		 * String username = getIntent().getStringExtra(AppInitial.USER_NAME);
		 * String score = getIntent().getStringExtra(AppInitial.SCORE); String
		 * rank = getIntent().getStringExtra(AppInitial.RANK);
		 */

		init();
	}

	private void init() {
		btnPlay = (Button) findViewById(R.id.btnPlay);

		btnPlay.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				if (httpConnection.checkNetWorkState(getApplicationContext())) {
					sendRequestGetMatch();
				} else {
					Toast toast = Toast.makeText(getApplicationContext(),
							"Not connection internet !", Toast.LENGTH_SHORT);
					toast.show();
				}

			}
		});
	}

	private void sendRequestGetMatch() {

		class SendGetMatchGetReqAsyncTask extends AsyncTask<Void, Void, String> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Authenticating user...");
				progressDialog.show();
			}

			@Override
			protected String doInBackground(Void... params) {

				HttpClient httpClient = httpConnection.getHttpClient();

				HttpGet httpGet = new HttpGet(AppInitial.getMatchUrl);

				try {

					HttpResponse httpResponse = httpClient.execute(httpGet);

					return httpConnection.getResponse(httpResponse);

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

				progressDialog.dismiss();
				if (result != null) {

					try {
						JSONObject reader = new JSONObject(result);

						int id = reader.getInt("id");
						String title = reader.getString("title");
						int level = reader.getInt("level");
						int competition = reader.getInt("competition");

						ArrayList<Item> items = new ArrayList<Item>();
						JSONArray itemsArray = reader.getJSONArray("items");
						for (int i = 0; i < itemsArray.length(); i++) {
							JSONObject jsonObject = itemsArray.getJSONObject(i);

							int idItem = jsonObject.getInt("id");
							String question = jsonObject.getString("question");
							String suggest = jsonObject.getString("suggest");
							String answer = jsonObject.getString("answer");
							boolean check = jsonObject.getBoolean("check");

							Item item = new Item(idItem, question, suggest,
									answer, check);
							items.add(item);
						}

						Match m = new Match(id, title, level, competition,
								items);

						Intent intent = new Intent(getApplicationContext(),
								PlayActivity.class);
						intent.putExtra("match", m);
						startActivity(intent);

					} catch (JSONException e) {

						e.printStackTrace();
					}

				}
			}

		}

		SendGetMatchGetReqAsyncTask sendGetMatchGetReqAsyncTask = new SendGetMatchGetReqAsyncTask();
		sendGetMatchGetReqAsyncTask.execute(null, null);

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
