package org.com.myapp.factory;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.Position;
import org.com.myapp.model.UserData;
import org.com.myapp.model.Word;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.os.AsyncTask;

public class MatchProcess {

	private final static int unit = 10;
	private int score;
	private int time;
	private List<Integer> itemId;

	private HttpConnection httpConnection = HttpConnection.getInstance();

	public int computeGrade(List<Integer> positions, ArrayList<Word> words) {

		int total = 0;
		for (Word w : words) {
			total = total + w.getItem().getAnswer().length();
		}

		int totalPositionCorrect = total - positions.size();

		return totalPositionCorrect * unit;
	}

	public List<Integer> getItemIdAnswerWrong(List<Integer> positions,
			ArrayList<Word> words) {

		ArrayList<Integer> idList = new ArrayList<Integer>();

		for (Integer i : positions) {

			Word word = getWord(i, words);
			if (word != null) {

				idList.add(word.getItem().getId());
			}

		}

		return idList;
	}

	public Word getWord(int position, ArrayList<Word> words) {

		for (Word w : words) {

			Position p = w.getPosition();

			int lenght = w.getItem().getAnswer().length();

			if (p.getDir() == 0) {

				int beginPosition = p.getY() + p.getX() * AppConfig.sizeBoard;
				int endPosition = beginPosition + lenght - 1;

				if (beginPosition <= position && position <= endPosition) {
					return w;
				}
			} else {

				int beginPosition = p.getY() + p.getX() * AppConfig.sizeBoard;
				int endPosition = beginPosition + AppConfig.sizeBoard
						* (p.getX() + lenght - 1);

				if (position >= beginPosition && position <= endPosition) {

					int mod1 = beginPosition % AppConfig.sizeBoard;
					int mod2 = position % AppConfig.sizeBoard;

					if (mod1 == mod2)
						return w;
				}

			}

		}
		return null;
	}

	public void sendRequestUpdateScore(int idMatch, int score, int time) {

		class SendPostRequestUpdateScore extends
				AsyncTask<Integer, Void, UserData> {

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

			@Override
			protected UserData doInBackground(Integer... params) {

				String paramMatchId = params[0] + "";
				String paramScore = params[1] + "";
				String paramTime = params[2] + "";

				try {

					RestTemplate restTemplate = httpConnection
							.getRestTemplate();

					RestTemplate temp = new RestTemplate();
					temp.setRequestFactory(restTemplate.getRequestFactory());

					List<HttpMessageConverter<?>> messageConverters = new LinkedList<HttpMessageConverter<?>>();

					messageConverters.add(new FormHttpMessageConverter());
					messageConverters.add(new StringHttpMessageConverter());
					messageConverters
							.add(new MappingJackson2HttpMessageConverter());
					temp.setMessageConverters(messageConverters);

					MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
					map.add("matchId", paramMatchId);
					map.add("score", paramScore);
					map.add("time", paramTime);

					HttpHeaders requestHeaders = new HttpHeaders();
					requestHeaders
							.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

					org.springframework.http.HttpEntity<MultiValueMap<String, String>> entity = new org.springframework.http.HttpEntity<MultiValueMap<String, String>>(
							map, requestHeaders);

					System.out.println(paramMatchId + " " + paramScore + " "
							+ paramTime);

					ResponseEntity<UserData> userData = temp.postForEntity(
							AppConfig.updateScoreUrl, entity, UserData.class);

					if (userData.getStatusCode() == HttpStatus.OK) {
						return userData.getBody();
					}

				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(UserData result) {
				super.onPostExecute(result);

				if (result != null) {

					System.out.println("User: " + result.getUsername()
							+ " Score: " + result.getScore());
				}
			}

		}

		SendPostRequestUpdateScore sendPostRequestUpdateScore = new SendPostRequestUpdateScore();
		sendPostRequestUpdateScore.execute(idMatch, score, time);
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

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public List<Integer> getItemId() {
		return itemId;
	}

	public void setItemId(List<Integer> itemId) {
		this.itemId = itemId;
	}

}
