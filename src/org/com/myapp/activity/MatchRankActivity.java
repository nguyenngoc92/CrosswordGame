package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.com.myapp.AppConfig;
import org.com.myapp.adapter.MatchRankListAdapter;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.UserData;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ListView;
import android.widget.Toast;

public class MatchRankActivity extends ActionBarActivity {

	private MatchRankListAdapter adapter;
	private ListView listView;
	private List<UserData> userDatas = new ArrayList<UserData>();
	private ProgressDialog progressDialog;

	private final HttpConnection httpConnection = HttpConnection.getInstance();
	private int matchId;
	private int lenght = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_rank);
		matchId = getIntent().getIntExtra("ID", 0);
		System.out.println(matchId);

		listView = (ListView) findViewById(R.id.listRank);

		if (matchId != 0) {

			// changing action bar color
			getActionBar().setBackgroundDrawable(
					new ColorDrawable(Color.parseColor("#1b1b1b")));

			if (httpConnection.checkNetWorkState(getApplicationContext())) {
				sendRequestGetListUserRankByMatch(matchId, lenght);

			} else {
				Toast toast = Toast.makeText(getApplicationContext(),
						"Not connection internet !", Toast.LENGTH_SHORT);
				toast.show();
			}

		}

	}

	private void sendRequestGetListUserRankByMatch(int id, int lenght) {

		class SendRequestGetListUserRankByMatch extends
				AsyncTask<Integer, Void, List<UserData>> {

			@Override
			protected void onPreExecute() {
				progressDialog = new ProgressDialog(MatchRankActivity.this);
				progressDialog.setMessage("Loading data ...");
				progressDialog.show();
				super.onPreExecute();
			}

			@Override
			protected List<UserData> doInBackground(Integer... params) {

				int id = params[0];
				int lenght = params[1];

				RestTemplate restTemplate = httpConnection.getRestTemplate();

				try {

					String url = AppConfig.getUserRankByMatch;

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());
					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("id", id + "");
					paramsMap.put("lenght", lenght + "");

					ResponseEntity<UserData[]> userEntity = restTemplate
							.getForEntity(url, UserData[].class, paramsMap);

					ArrayList<UserData> list = new ArrayList<UserData>();
					if (userEntity.getStatusCode() == HttpStatus.OK) {

						list.addAll(Arrays.asList(userEntity.getBody()));
					}

					return list;

				} catch (HttpClientErrorException e) {

					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(List<UserData> result) {

				if (result != null) {

					System.out.println(result.size());
					userDatas.addAll(result);
					adapter = new MatchRankListAdapter(MatchRankActivity.this,
							userDatas);
					listView.setAdapter(adapter);
				}
				super.onPostExecute(result);
				progressDialog.dismiss();
			}

		}

		SendRequestGetListUserRankByMatch request = new SendRequestGetListUserRankByMatch();
		request.execute(id, lenght);

	}

}
