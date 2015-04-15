package org.com.myapp.inet;

import org.com.myapp.AppConfig;
import org.com.myapp.model.UserData;
import org.springframework.web.client.RestTemplate;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;

public class HttpRequest {

	private Context context;
	private Dialog dialog;
	
	private final HttpConnection httpConnection = HttpConnection.getInstance();
	
	
	
	public HttpRequest(Context context){
		this.context = context;
	}
	
	
	public void getUserInfor() {
		class SendRequestGetUserInfor extends AsyncTask<Void, Void, UserData> {

			@Override
			protected UserData doInBackground(Void... params) {

				RestTemplate restTemplate = httpConnection.getRestTemplate();

				UserData userData = restTemplate.getForObject(
						AppConfig.userInforUrl, UserData.class);

				return userData;
			}

			@Override
			protected void onPostExecute(UserData result) {

				super.onPostExecute(result);

				if (result != null) {
					System.out.println("User: " + result.getUsername());
				}
			}

		}

		SendRequestGetUserInfor sendRequestGetUserInfor = new SendRequestGetUserInfor();
		sendRequestGetUserInfor.execute();
	}

}
