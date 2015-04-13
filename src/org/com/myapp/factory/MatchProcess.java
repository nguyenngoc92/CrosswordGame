package org.com.myapp.factory;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.com.myapp.AppInitial;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

				idList.add(word.getItem().getIdItem());
			}

		}

		return idList;
	}

	public Word getWord(int position, ArrayList<Word> words) {

		for (Word w : words) {

			Position p = w.getPosition();

			int lenght = w.getItem().getAnswer().length();

			if (p.getDir() == 0) {

				int beginPosition = p.getY() + p.getX() * AppInitial.sizeBoard;
				int endPosition = beginPosition + lenght - 1;

				if (beginPosition <= position && position <= endPosition) {
					return w;
				}
			} else {

				int beginPosition = p.getY() + p.getX() * AppInitial.sizeBoard;
				int endPosition = beginPosition + AppInitial.sizeBoard
						* (p.getX() + lenght - 1);

				if (position >= beginPosition && position <= endPosition) {

					int mod1 = beginPosition % AppInitial.sizeBoard;
					int mod2 = position % AppInitial.sizeBoard;

					if (mod1 == mod2)
						return w;
				}

			}

		}
		return null;
	}

	public void sendRequestUpdateScore(int idMatch, int score, int time) {

		class SendUpdateScoreGetReqAsyncTask extends
				AsyncTask<Integer, Void, HttpStatus> {

			@Override
			protected HttpStatus doInBackground(Integer... params) {

				int match = params[0];
				int score = params[1];
				int time = params[2];
				RestTemplate restTemplate = new RestTemplate();

				MultiValueMap<String, Object> map = new LinkedMultiValueMap<String, Object>();
				map.add("matchId", match);
				map.add("score", score);
				map.add("time", time);
				map.add("unit", "minute");

				HttpStatus http = restTemplate.postForObject(
						AppInitial.updateScoreurl, null, HttpStatus.class, map);

				return http;
			}

			@Override
			protected void onPostExecute(HttpStatus result) {

				super.onPostExecute(result);
			}

		}

		SendUpdateScoreGetReqAsyncTask sendUpdateScoreGetReqAsyncTask = new SendUpdateScoreGetReqAsyncTask();
		sendUpdateScoreGetReqAsyncTask.execute(idMatch, score, time);
	}

	public void sendRequestUpdateInforItem(List<Integer> items) {

		class SendRequestUpdateItemTask extends
				AsyncTask<List<Integer>, Void, String> {

			@Override
			protected String doInBackground(List<Integer>... params) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
			}

		}

		SendRequestUpdateItemTask sendRequestUpdateItemTask = new SendRequestUpdateItemTask();
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
