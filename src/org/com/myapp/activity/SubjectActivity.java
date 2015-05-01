package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.adapter.GridSubjectAdapter;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.SubjectData;
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
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class SubjectActivity extends ActionBarActivity {

	private GridView gridView;
	private GridSubjectAdapter subjectAdapter;
	private List<SubjectData> dataList = new ArrayList<SubjectData>();
	private HttpConnection httpConnection = HttpConnection.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_subject);
		this.initial();
	}

	private void initial() {

		gridView = (GridView) findViewById(R.id.gridSubject);
		subjectAdapter = new GridSubjectAdapter(SubjectActivity.this, dataList);
		gridView.setAdapter(subjectAdapter);

		if (httpConnection.checkNetWorkState(getApplicationContext())) {

			sendRequestGetAllSubject();

		} else {

			Toast toast = Toast.makeText(getApplicationContext(),
					"Not connection internet !", Toast.LENGTH_SHORT);
			toast.show();
			this.finish();

		}

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {

				System.out.println(dataList.get(position).getName());

				SubjectData subject = dataList.get(position);

				if (subject != null) {
					Intent intent = new Intent(SubjectActivity.this,
							PlayActivity.class);
					intent.putExtra(AppConfig.FLAG, AppConfig.FLAG_SUBJECT);
					intent.putExtra("ID", subject.getIdSubject());
					startActivity(intent);
				}

			}
		});

	}

	private void sendRequestGetAllSubject() {

		class SendRequestGetAllSubject extends
				AsyncTask<Void, Void, List<SubjectData>> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				progressDialog = new ProgressDialog(SubjectActivity.this);
				progressDialog.setMessage("Loading data...");
				progressDialog.show();

			}

			@Override
			protected List<SubjectData> doInBackground(Void... params) {

				RestTemplate restTemplate = httpConnection.getRestTemplate();
				try {

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					ResponseEntity<SubjectData[]> subEntity = restTemplate
							.getForEntity(AppConfig.getAllSubjectUrl,
									SubjectData[].class);

					ArrayList<SubjectData> subList = new ArrayList<SubjectData>();
					if (subEntity.getStatusCode() == HttpStatus.OK) {

						subList.addAll(Arrays.asList(subEntity.getBody()));
					}

					return subList;
				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(List<SubjectData> result) {
				super.onPostExecute(result);

				if (result != null) {
					dataList.addAll(result);
					subjectAdapter.notifyDataSetChanged();
				}
				progressDialog.dismiss();

			}

		}
		SendRequestGetAllSubject requestGetAllSubject = new SendRequestGetAllSubject();
		requestGetAllSubject.execute();

	}

}
