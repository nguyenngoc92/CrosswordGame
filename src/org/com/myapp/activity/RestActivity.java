package org.com.myapp.activity;

import org.com.myapp.model.UserData;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RestActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rest);
		this.init();
	}

	public void init() {
		Button btn = (Button) findViewById(R.id.buttonRest);
		btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				System.out.println("------------------------------------");
				new HttpRequestTask().execute();

			}
		});
	}

	private class HttpRequestTask extends AsyncTask<Void, Void, UserData> {
		@Override
		protected UserData doInBackground(Void... params) {
			try {
				final String userInforUrl = "http://192.168.1.69:8080/user/getUser";
				RestTemplate restTemplate = new RestTemplate();
				restTemplate.getMessageConverters().add(
						new MappingJackson2HttpMessageConverter());
				UserData user = restTemplate.getForObject(userInforUrl,
						UserData.class);
				return user;
			} catch (Exception e) {
				Log.e("MainActivity", e.getMessage(), e);
			}

			return null;
		}

		@Override
		protected void onPostExecute(UserData user) {

			System.out.println("------------------------------------");
			Toast t = Toast.makeText(getApplicationContext(),
					user.getUsername(), Toast.LENGTH_SHORT);
			t.show();
		}

	}

}
