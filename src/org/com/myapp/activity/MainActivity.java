package org.com.myapp.activity;

import org.com.myapp.inet.HttpConnection;

import android.app.ProgressDialog;
import android.content.Intent;
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
