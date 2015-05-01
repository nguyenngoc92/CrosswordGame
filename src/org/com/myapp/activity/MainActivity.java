package org.com.myapp.activity;

import java.text.SimpleDateFormat;
import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.CompetitionData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
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

					Intent intent = new Intent(getApplicationContext(),
							SubjectActivity.class);
					startActivity(intent);
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

		Button btnCompetition = (Button) findViewById(R.id.btnCompetition);
		btnCompetition.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				sendRequestGetCurrentCompetition();
			}
		});
	}

	private void sendRequestGetCurrentCompetition() {
		class SendRequestGetCurrentCompetition extends
				AsyncTask<Void, Void, CompetitionData> {

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MainActivity.this);
				progressDialog.setMessage("Loading data...");
				progressDialog.show();

				super.onPreExecute();
			}

			@Override
			protected CompetitionData doInBackground(Void... params) {
				try {

					RestTemplate restTemplate = httpConnection
							.getRestTemplate();
					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					ResponseEntity<CompetitionData> entity = restTemplate
							.getForEntity(AppConfig.getCurrentCompetitionUrl,
									CompetitionData.class);

					if (entity.getStatusCode() == HttpStatus.OK) {

						return entity.getBody();
					}

				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}
				return null;
			}

			@SuppressLint("SimpleDateFormat")
			@Override
			protected void onPostExecute(final CompetitionData competitionData) {
				super.onPostExecute(competitionData);
				progressDialog.dismiss();

				if (competitionData != null) {

					final Dialog dialog = new Dialog(MainActivity.this);
					dialog.setContentView(R.layout.dialog_competition);
					dialog.setTitle("Let try with your knowledge !");

					TextView tvTitle = (TextView) dialog
							.findViewById(R.id.tvTitle);
					TextView tvStart = (TextView) dialog
							.findViewById(R.id.tvStart);
					TextView tvEnd = (TextView) dialog.findViewById(R.id.tvEnd);
					TextView tvDescrible = (TextView) dialog
							.findViewById(R.id.tvDescrible);
					Button btnDialog = (Button) dialog
							.findViewById(R.id.btnTakePartIn);

					tvTitle.setText(competitionData.getName());
					SimpleDateFormat formatter = new SimpleDateFormat(
							"dd/MM/yyyy HH:mm:ss");
					String start = formatter.format(competitionData.getBegin());
					String end = formatter.format(competitionData.getEnd());
					tvStart.setText(start);
					tvEnd.setText(end);
					tvDescrible.setText(competitionData.getNote());

					btnDialog.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View view) {

							Intent intent = new Intent(MainActivity.this,
									PlayActivity.class);
							intent.putExtra(AppConfig.FLAG,
									AppConfig.FLAG_COMPETITION);
							intent.putExtra("ID",
									competitionData.getIdCompetition());
							startActivity(intent);
						}
					});
					dialog.show();

				}

			}

		}

		SendRequestGetCurrentCompetition requestGetCurrentCompetition = new SendRequestGetCurrentCompetition();
		requestGetCurrentCompetition.execute();
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
