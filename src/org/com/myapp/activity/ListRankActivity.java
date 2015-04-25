package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.com.myapp.AppConfig;
import org.com.myapp.adapter.RankListAdapter;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

public class ListRankActivity extends ActionBarActivity {

	private RankListAdapter adapter;
	private ListView listView;
	private List<UserData> userDatas = new ArrayList<UserData>();
	private ProgressDialog progressDialog;

	private final HttpConnection httpConnection = HttpConnection.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rank);

		listView = (ListView) findViewById(R.id.listRank);

		adapter = new RankListAdapter(this, userDatas);
		listView.setAdapter(adapter);

		// changing action bar color
		getActionBar().setBackgroundDrawable(
				new ColorDrawable(Color.parseColor("#1b1b1b")));
		
		

		if (httpConnection.checkNetWorkState(getApplicationContext())) {
			sendRequestGetListUserRank(0, 10);
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Not connection internet !", Toast.LENGTH_SHORT);
			toast.show();
		}

		listView.setOnTouchListener(new View.OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return false;
			}
		});

	}

	private void sendRequestGetListUserRank(int row, int limit) {

		class SendRequestGetListUserRank extends
				AsyncTask<Integer, Void, List<UserData>> {

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(ListRankActivity.this);
				progressDialog.setMessage("Loading data ...");
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

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());
					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("id", row + "");
					paramsMap.put("limit", limit + "");

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

					userDatas.addAll(result);
					adapter.notifyDataSetChanged();
				}
				super.onPostExecute(result);
				progressDialog.dismiss();

			}

		}

		SendRequestGetListUserRank request = new SendRequestGetListUserRank();
		request.execute(row, limit);
	}

}
