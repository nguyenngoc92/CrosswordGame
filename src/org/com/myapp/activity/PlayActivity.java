package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.com.myapp.AppConfig;
import org.com.myapp.adapter.GridAdapter;
import org.com.myapp.factory.CrossWordFactory;
import org.com.myapp.inet.HttpConnection;
import org.com.myapp.keyboard.KeyboardView;
import org.com.myapp.keyboard.KeyboardViewInterface;
import org.com.myapp.model.Cell;
import org.com.myapp.model.Direction;
import org.com.myapp.model.ItemData;
import org.com.myapp.model.MatchData;
import org.com.myapp.model.MatchItemForm;
import org.com.myapp.model.Position;
import org.com.myapp.model.ScoreForm;
import org.com.myapp.model.SubjectData;
import org.com.myapp.model.UserData;
import org.com.myapp.model.Word;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class PlayActivity extends ActionBarActivity implements
		KeyboardViewInterface, OnTouchListener {

	public enum GRID_MODE {
		NORMAL, CHECK, SOLVE
	};

	public GRID_MODE currentMode = GRID_MODE.NORMAL;

	private CrossWordFactory factory;

	private GridView grid;
	private TextView tvDescription;
	private KeyboardView keyboardView;
	private TextView keyboardOverlay;

	private GridAdapter gridAdapter;

	private ArrayList<Word> words = new ArrayList<Word>();

	private Cell[][] gridCell;

	private int gridSize;

	private ArrayList<Word> wordList = new ArrayList<Word>();

	private ArrayList<View> selectedArea = new ArrayList<View>();
	private ArrayList<View> currentArea = new ArrayList<View>();

	private boolean dowIsPlayable;

	private int previousPosition = -1;

	private int currentDir = -1;

	private Word tmpWord;

	private View view;

	private double initialTime;

	private MatchData m;

	private SubjectData subject;
	private HttpConnection httpConnection = HttpConnection.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_play);

		this.subject = (SubjectData) getIntent()
				.getSerializableExtra("Subject");

		this.init();
	}

	private void init() {

		this.grid = (GridView) findViewById(R.id.grid);
		this.tvDescription = (TextView) findViewById(R.id.tvDescription);
		this.keyboardView = (KeyboardView) findViewById(R.id.keyboard);

		if (httpConnection.checkNetWorkState(PlayActivity.this)) {
			this.resetGrid(subject.getIdSubject());
		} else {
			Toast toast = Toast.makeText(getApplicationContext(),
					"Not connection internet !", Toast.LENGTH_SHORT);
			toast.show();
			this.finish();
		}

	}

	public void resetGrid(int idSubject) {

		this.words = new ArrayList<Word>();
		this.wordList = new ArrayList<Word>();
		this.m = null;
		this.gridCell = null;
		selectedArea = new ArrayList<View>();
		currentArea = new ArrayList<View>();
		this.tmpWord = null;
		this.previousPosition = -1;
		this.currentDir = -1;
		this.tvDescription.setText(null);
		this.initialTime = 0;

		sendRequestGetMatch(idSubject);

	}

	private void createGrid(ArrayList<Word> words) {

		factory = new CrossWordFactory(words);

		gridCell = factory.pickBestGrid();

		if (gridCell != null) {
			this.gridSize = gridCell.length;
			wordList.addAll(factory.getWords());

			Display display = getWindowManager().getDefaultDisplay();
			@SuppressWarnings("deprecation")
			int height = display.getHeight();
			int keyboardHeight = (int) (height / 4.4);
			android.view.ViewGroup.LayoutParams gridParams = this.grid
					.getLayoutParams();
			gridParams.height = height - keyboardHeight
					- this.tvDescription.getLayoutParams().height;

			this.gridAdapter = new GridAdapter(this, gridCell);
			this.grid.setAdapter(gridAdapter);
			this.grid.setNumColumns(gridSize);
			this.grid.setLayoutParams(gridParams);
			this.grid.setVerticalScrollBarEnabled(false);
			this.grid.setOnTouchListener(this);

			this.keyboardView = (KeyboardView) findViewById(R.id.keyboard);
			this.keyboardView.setDelegate(this);

			android.view.ViewGroup.LayoutParams KeyboardParams = this.keyboardView
					.getLayoutParams();
			KeyboardParams.height = keyboardHeight;
			this.keyboardView.setLayoutParams(KeyboardParams);

			this.keyboardOverlay = (TextView) findViewById(R.id.keyboard_overlay);

			this.initialTime = System.currentTimeMillis();
		} else {
			System.out
					.println("==========================================================");
			Toast t = Toast.makeText(this, "Error create grid for crossword",
					Toast.LENGTH_LONG);
			t.show();
			this.finish();
		}

	}

	private ArrayList<Word> createWordList(MatchData match) {

		ArrayList<Word> wArrayList = new ArrayList<Word>();

		List<ItemData> items = match.getItems();
		Collections.sort(items);
		for (int i = 0; i < items.size(); i++) {

			Word w = new Word(i + 1, new Position(), items.get(i));
			wArrayList.add(w);

		}

		return wArrayList;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.game_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_check) {

			this.currentMode = GRID_MODE.CHECK;
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Do you want to finish ?");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							currentMode = GRID_MODE.CHECK;
							double endTime = System.currentTimeMillis();
							List<Integer> positions = getListPositionWrong();
							showError(positions);
							gridAdapter.notifyDataSetChanged();

							float score = calculateGrade(getWordRight());

							Toast t = Toast.makeText(getApplicationContext(),
									score + "", Toast.LENGTH_SHORT);
							t.show();

							float time = (float) ((endTime - initialTime) / (60 * 1000));

							ScoreForm scoreForm = new ScoreForm(m.getId(),
									score, time);
							MatchItemForm matchItem = createMatchItemForm(m);

							if (httpConnection
									.checkNetWorkState(PlayActivity.this)) {
								sendRequestUpdateGrade(scoreForm);
								if (matchItem.getIdList().size() != 0) {

									sendRequestUpdateQuestionInfor(matchItem);
								}

							} else {
								Toast toast = Toast
										.makeText(
												getApplicationContext(),
												"Not connection internet !\n Your grade cannot save !",
												Toast.LENGTH_SHORT);
								toast.show();
							}

						}

					});

			dialog.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							dialog.cancel();
						}
					});

			dialog.create().show();

			return true;
		}

		if (id == R.id.action_solve) {

			if (currentMode == GRID_MODE.CHECK) {

				this.currentMode = GRID_MODE.SOLVE;

			} else {

				Toast t = Toast.makeText(this,
						"You must finish your match !!!", Toast.LENGTH_SHORT);
				t.show();
			}

			return true;

		}

		if (id == R.id.action_myRank) {

			Intent intent = new Intent(PlayActivity.this, MyRankActivity.class);
			intent.putExtra("MATCHID", m.getId());
			startActivity(intent);
		}

		if (id == R.id.action_getRankByMatch) {

			Intent intent = new Intent(getApplicationContext(),
					MatchRankActivity.class);
			intent.putExtra("ID", m.getId());
			startActivity(intent);

		}

		if (id == R.id.action_play_continue) {
			this.resetGrid(subject.getIdSubject());
		}

		return super.onOptionsItemSelected(item);
	}

	protected MatchItemForm createMatchItemForm(MatchData matchData) {

		ArrayList<Integer> idList = new ArrayList<Integer>();
		for (ItemData item : m.getItems()) {

			if (item.isCheck()) {
				idList.add(item.getId());
			}
		}

		return new MatchItemForm(m.getId(), idList);
	}

	protected void showError(List<Integer> positions) {

		for (Integer p : positions) {

			TextView v = (TextView) grid.getChildAt(p);

			if (v != null) {
				v.setTextColor(this.getResources().getColor(R.color.wrong));
			}

		}

	}

	protected List<Integer> getListPositionWrong() {

		String answer[][] = gridAdapter.getAnswer();

		List<Integer> list = new ArrayList<Integer>();
		for (Word w : wordList) {

			int r = w.getRow();
			int c = w.getCol();
			int rMax = w.getMaxRow();
			int cMax = w.getMaxCol();
			if (w.getDirection() == Direction.ACROSS) {

				for (int i = 0; i <= cMax - c; i++) {
					if ((w.getAnswer().charAt(i) + "") != answer[r][c + i]) {

						int p = r * gridSize + c + i;
						list.add(p);
					}
				}

			} else if (w.getDirection() == Direction.DOWN) {

				for (int i = 0; i <= rMax - r; i++) {
					if ((w.getAnswer().charAt(i) + "") != answer[r + i][c]) {

						int p = (r + i) * gridSize + c;
						list.add(p);
					}
				}
			}

		}

		return list;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int x = (int) event.getX();
		int y = (int) event.getY();
		int itemPosition = grid.pointToPosition(x, y);

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {

			View child = grid.getChildAt(itemPosition);

			if (child == null || child.getTag().equals(GridAdapter.AREA_BLOCK)) {

				this.dowIsPlayable = false;
				return true;
			}

			boolean hDir = false;
			boolean vDir = false;

			hDir = checkHashLeftOrRightChild(itemPosition);
			vDir = checkHashUnderOrUpChild(itemPosition);

			if (hDir) {
				setCurrentDir(0);
			} else if (vDir) {

				setCurrentDir(1);
			} else {
				setCurrentDir(-1);
			}

			clearLineClicked();
			setLineClicked(itemPosition);
			this.dowIsPlayable = true;

			if (view != null) {
				resetViewSelected(itemPosition);
			}

			view = child;
			child.setBackgroundResource(R.drawable.area_current);
			selectedArea.add(child);
			this.gridAdapter.notifyDataSetChanged();
			this.previousPosition = itemPosition;

			break;
		}

		case MotionEvent.ACTION_UP: {

			if (!this.dowIsPlayable) {

				return true;
			}

			String title = tmpWord.getItem().getQuestion();
			this.tvDescription.setText(title);

			this.gridAdapter.notifyDataSetChanged();

			break;
		}
		}
		return true;
	}

	private void resetViewSelected(int position) {

		if (currentDir == 0) {

			if (position / this.gridSize == previousPosition / this.gridSize) {
				view.setBackgroundResource(R.drawable.area_selected);
			} else {
				view.setBackgroundResource(R.drawable.area_empty);
			}

		} else if (currentDir == 1) {

			if (position % this.gridSize == previousPosition % this.gridSize) {
				view.setBackgroundResource(R.drawable.area_selected);
			} else {
				view.setBackgroundResource(R.drawable.area_empty);
			}
		}

	}

	private void setLineClicked(int position) {
		Word currentWord = getCurrentWord(currentDir, position);
		if (currentWord != null) {

			tmpWord = currentWord;

			if (currentDir == 0) {

				for (int i = 0; i < currentWord.getItem().getAnswer().length(); i++) {

					Position p = currentWord.getPosition();
					int begin = p.getC() + this.gridSize * p.getR();
					View v = grid.getChildAt(begin + i);
					v.setBackgroundResource(R.drawable.area_selected);
					currentArea.add(v);
				}

			} else if (currentDir == 1) {

				for (int i = 0; i < currentWord.getItem().getAnswer().length(); i++) {

					Position p = currentWord.getPosition();
					int begin = p.getC() + this.gridSize * p.getR();
					View v = grid.getChildAt(begin + i * this.gridSize);
					v.setBackgroundResource(R.drawable.area_selected);
					currentArea.add(v);
				}
			}

		}

	}

	private Word getCurrentWord(int currentDir, int position) {

		if (currentDir == 0) {

			for (Word w : wordList) {

				Position p = w.getPosition();

				int lenght = w.getItem().getAnswer().length();

				int dir = p.getDir() == Direction.ACROSS ? 0 : 1;
				if (dir == 0) {

					int beginPosition = p.getC() + p.getR() * this.gridSize;
					int endPosition = beginPosition + lenght - 1;

					if (position >= beginPosition && position <= endPosition) {

						return w;
					}

				}
			}

		} else if (currentDir == 1) {

			for (Word w : wordList) {
				Position p = w.getPosition();

				int lenght = w.getItem().getAnswer().length();
				int dir = p.getDir() == Direction.ACROSS ? 0 : 1;
				if (dir == 1) {
					int beginPosition = p.getC() + p.getR() * this.gridSize;
					int endPosition = beginPosition + this.gridSize
							* (p.getR() + lenght - 1);

					if (position >= beginPosition && position <= endPosition) {

						int mod1 = beginPosition % this.gridSize;
						int mod2 = position % this.gridSize;

						if (mod1 == mod2)
							return w;
					}
				}

			}
		}

		return null;
	}

	private void clearLineClicked() {

		for (View v : currentArea) {

			v.setBackgroundResource(R.drawable.area_empty);
		}

		currentArea.removeAll(currentArea);

	}

	private void setCurrentDir(int i) {

		this.currentDir = i;
	}

	private boolean checkHashUnderOrUpChild(int position) {
		View upChild = this.grid.getChildAt(position - this.gridSize);
		View underChild = this.grid.getChildAt(position + this.gridSize);
		if (upChild != null || underChild != null) {
			if (upChild != null) {
				if (upChild.getTag().equals(GridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}

			if (underChild != null) {
				if (underChild.getTag().equals(GridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}
		}

		return false;
	}

	private boolean checkHashLeftOrRightChild(int position) {

		View leftChild = this.grid.getChildAt(position - 1);
		View rightChild = this.grid.getChildAt(position + 1);

		if (leftChild != null || rightChild != null) {

			if (leftChild != null)
				if (leftChild.getTag().equals(GridAdapter.AREA_WRITABLE)) {
					return true;
				}

			if (rightChild != null) {
				if (rightChild.getTag().equals(GridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onKeyDown(String value, int[] location, int width) {
		// Deplace l'overlay du clavier
		if (value.equals(" ") == false) {
			int offsetX = (this.keyboardOverlay.getWidth() - width) / 2;
			int offsetY = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					AppConfig.KEYBOARD_OVERLAY_OFFSET, getResources()
							.getDisplayMetrics());
			FrameLayout.LayoutParams lp = (LayoutParams) this.keyboardOverlay
					.getLayoutParams();
			lp.leftMargin = location[0] - offsetX;
			lp.topMargin = location[1] - offsetY;
			this.keyboardOverlay.setLayoutParams(lp);
			this.keyboardOverlay.setText(value);
			this.keyboardOverlay.clearAnimation();
			this.keyboardOverlay.setVisibility(View.VISIBLE);
		}

	}

	@Override
	public void onKeyUp(String value) {

		if (value.equals(" ") == false) {
			this.keyboardOverlay.setAnimation(AnimationUtils.loadAnimation(
					this, R.anim.keyboard_overlay_fade_out));
			this.keyboardOverlay.setVisibility(View.INVISIBLE);
		}

		if (currentMode == GRID_MODE.NORMAL) {
			if (tmpWord == null)
				return;

			if (this.gridAdapter.isBlock(previousPosition)) {

				return;
			}

			this.gridAdapter.setValue(previousPosition, value);
			this.gridAdapter.notifyDataSetChanged();

			if (view != null) {
				TextView tView = (TextView) view;
				tView.setText(value);
			}

			this.gridAdapter.notifyDataSetChanged();
			// move cursor to the next point
			int tmp = this.moveItemSelected(value);

			if (tmp >= 1 && tmp <= this.gridSize * this.gridSize
					&& !gridAdapter.isBlock(tmp)) {

				view.setBackgroundResource(R.drawable.area_selected);
				view = grid.getChildAt(tmp);
				view.setBackgroundResource(R.drawable.area_current);
				previousPosition = tmp;

			}

			this.gridAdapter.notifyDataSetChanged();
		}

	}

	private int moveItemSelected(String value) {

		int tmp = -1;
		if (previousPosition != -1) {

			if (currentDir != -1) {

				if (value.equals(" ")) {

					tmp = this.currentDir == 0 ? previousPosition
							- this.gridSize : previousPosition - 1;
				} else {

					tmp = this.currentDir == 0 ? previousPosition + 1
							: previousPosition + this.gridSize;
				}

			}
		}

		return tmp;
	}

	@Override
	public void setDraft(boolean isDraft) {

	}

	private List<Word> getWordRight() {

		String[][] answer = this.gridAdapter.getAnswer();
		ArrayList<Word> wordRights = new ArrayList<Word>();
		for (Word word : wordList) {

			boolean check = this.checkWordRight(word, answer);

			if (check) {
				wordRights.add(word);
				int id = word.getItem().getId();
				List<ItemData> items = m.getItems();
				for (ItemData item : items) {
					if (item.getId() == id) {
						item.setCheck(true);
					} else {
						item.setCheck(false);
					}
				}

			}

		}

		return wordRights;

	}

	private float calculateGrade(List<Word> wordAnswers) {

		List<Word> hList = new ArrayList<Word>();
		List<Word> vList = new ArrayList<Word>();
		int countCommon = 0;
		int totalLetter = 0;

		for (Word w : wordAnswers) {
			if (w.getDirection() == Direction.ACROSS) {
				hList.add(w);
				totalLetter += w.getAnswer().length();
			} else if (w.getDirection() == Direction.DOWN) {
				vList.add(w);
				totalLetter += w.getAnswer().length();
			}
		}

		for (int i = 0; i < hList.size(); i++) {

			Word hWord = hList.get(i);
			for (Word w : vList) {

				if (w.getRow() < hWord.getRow()
						&& hWord.getRow() < w.getMaxRow()
						&& w.getCol() > hWord.getCol()
						&& w.getCol() < hWord.getMaxCol()) {
					countCommon++;
				}

			}
		}

		int totalGrade = (totalLetter - countCommon) * 10;

		return (float) totalGrade;
	}

	private boolean checkWordRight(Word word, String[][] answer) {

		int r = word.getRow();
		int c = word.getCol();

		if (word.getDirection() == Direction.ACROSS) {

			for (int i = 0; i < word.getAnswer().length(); i++) {

				if ((word.getAnswer().charAt(i) + "") != answer[r][c + i]) {

					return false;
				}
			}

		} else if (word.getDirection() == Direction.DOWN) {

			for (int i = 0; i < word.getAnswer().length(); i++) {

				if ((word.getAnswer().charAt(i) + "") != answer[r + i][c]) {

					return false;
				}
			}

		}
		return true;
	}

	private void solveAnswer() {

	}

	private void sendRequestGetMatch(int idSubject) {

		class SendRequestGetMatch extends AsyncTask<Integer, Void, MatchData> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				progressDialog = new ProgressDialog(PlayActivity.this);
				progressDialog.setMessage("Loading data...");
				progressDialog.show();
			}

			@Override
			protected MatchData doInBackground(Integer... params) {

				int idsubject = params[0];
				RestTemplate restTemplate = httpConnection.getRestTemplate();
				try {

					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());
					Map<String, String> paramsMap = new HashMap<String, String>();
					paramsMap.put("idsubject", idsubject + "");

					MatchData matchData = restTemplate.getForObject(
							AppConfig.getMatchBySubjectUrl, MatchData.class,
							paramsMap);
					return matchData;
				} catch (HttpClientErrorException e) {

				}

				return null;
			}

			@Override
			protected void onPostExecute(MatchData match) {
				// TODO Auto-generated method stub
				super.onPostExecute(match);

				if (match != null && match.getItems() != null
						&& match.getItems().size() != 0) {

					System.out.println(match.getId() + " Size "
							+ match.getItems().size());
					m = match;
					words.addAll(createWordList(m));
					createGrid(words);

				}

				progressDialog.dismiss();
			}

		}

		SendRequestGetMatch requestGetMatch = new SendRequestGetMatch();
		requestGetMatch.execute(idSubject);

	}

	private void sendRequestUpdateQuestionInfor(MatchItemForm matchItem) {

		class SendRequestUpdateQuestionInfor extends
				AsyncTask<MatchItemForm, Void, ResponseEntity<String>> {

			@Override
			protected ResponseEntity<String> doInBackground(
					MatchItemForm... params) {

				try {

					RestTemplate restTemplate = httpConnection
							.getRestTemplate();
					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());

					MatchItemForm matchItem = params[0];

					ResponseEntity<String> result = restTemplate.postForEntity(
							AppConfig.updateItemInforAnswer, matchItem,
							String.class);

					return result;
				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

		}

		SendRequestUpdateQuestionInfor requestUpdateQuestionInfor = new SendRequestUpdateQuestionInfor();
		requestUpdateQuestionInfor.execute(matchItem);
	}

	protected void sendRequestUpdateGrade(ScoreForm scoreForm) {
		// TODO Auto-generated method stub

		class SendRequestUpdateGrade extends
				AsyncTask<ScoreForm, Void, UserData> {

			ProgressDialog progressDialog;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				progressDialog = new ProgressDialog(PlayActivity.this);
				progressDialog.setMessage("Please wait...");
				progressDialog.show();
			}

			@Override
			protected UserData doInBackground(ScoreForm... params) {
				// TODO Auto-generated method stub

				ScoreForm scoreForm = params[0];

				try {

					RestTemplate restTemplate = httpConnection
							.getRestTemplate();
					restTemplate.getMessageConverters().add(
							new MappingJackson2HttpMessageConverter());
					ResponseEntity<UserData> entity = restTemplate
							.postForEntity(AppConfig.updateScoreUrl, scoreForm,
									UserData.class);

					if (entity.getStatusCode() == HttpStatus.OK) {

						return entity.getBody();
					}

				} catch (HttpClientErrorException e) {
					e.printStackTrace();
				}

				return null;
			}

			@Override
			protected void onPostExecute(UserData result) {
				// TODO Auto-generated method stub
				super.onPostExecute(result);
				progressDialog.dismiss();

				if (result != null) {
					final Dialog dialog = new Dialog(PlayActivity.this);
					dialog.setContentView(R.layout.dialog_score);
					dialog.setTitle("Congratulation you !");

					TextView tvTitle = (TextView) dialog
							.findViewById(R.id.tvDialogTitle);
					TextView tvScore = (TextView) dialog
							.findViewById(R.id.tvDialogScore);
					TextView tvTime = (TextView) dialog
							.findViewById(R.id.tvDialogTime);
					TextView tvRank = (TextView) dialog
							.findViewById(R.id.tvDialogRank);
					Button btnDialog = (Button) dialog
							.findViewById(R.id.btnDialog);

					tvTitle.setText(result.getUsername());
					tvScore.setText(result.getScore() + "");
					tvTime.setText(result.getTime() + "");
					tvRank.setText(result.getRank() + "");
					dialog.setCancelable(true);

					btnDialog.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							dialog.dismiss();

						}
					});
					dialog.show();
				} else {
					System.out.println("RESULT: NULL");
				}

			}

		}

		SendRequestUpdateGrade requestUpdateGrade = new SendRequestUpdateGrade();
		requestUpdateGrade.execute(scoreForm);
	}

}