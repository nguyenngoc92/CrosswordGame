package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.com.myapp.AppInitial;
import org.com.myapp.adapter.GameGridAdapter;
import org.com.myapp.factory.CrossWordFactory;
import org.com.myapp.factory.MatchProcess;
import org.com.myapp.keyboard.KeyboardView;
import org.com.myapp.keyboard.KeyboardViewInterface;
import org.com.myapp.model.ItemData;
import org.com.myapp.model.MatchData;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.GridView;
import android.widget.TextView;

public class PlayActivity extends ActionBarActivity implements OnTouchListener,
		KeyboardViewInterface {

	public enum GRID_MODE {
		NORMAL, CHECK, SOLVE
	};

	public GRID_MODE currentMode = GRID_MODE.NORMAL;

	private GridView gridView;
	private KeyboardView keyboardView;
	private GameGridAdapter gridAdapter;
	private TextView txtDescription;
	private TextView keyboardOverlay;

	private ArrayList<Word> entries;
	private ArrayList<View> selectedArea = new ArrayList<View>();
	private ArrayList<View> currentArea = new ArrayList<View>();

	private int width = AppInitial.sizeBoard;
	private int height = AppInitial.sizeBoard;

	private boolean dowIsPlayable;

	private int previousPosition = -1;

	private int currentDir = -1;

	private CrossWordFactory factory;

	private Word tmpWord;

	private View view;

	private double initialTime;
	
	
	private MatchData m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		m = (MatchData) getIntent().getSerializableExtra("match");

		if (m != null) {
			entries = (ArrayList<Word>) createListWord(m);
		}
		factory = new CrossWordFactory(width, height);

		for (Word w : entries) {
			w.setPosition(factory.addWord(w.getItem().getAnswer()));
		}

		this.initialTime = System.currentTimeMillis();

		Display display = getWindowManager().getDefaultDisplay();
		@SuppressWarnings("deprecation")
		int height = display.getHeight();
		int keyboardHeight = (int) (height / 4.4);

		this.txtDescription = (TextView) findViewById(R.id.description);

		this.gridView = (GridView) findViewById(R.id.grid);
		this.gridView.setOnTouchListener(this);
		this.gridView.setNumColumns(this.width);
		android.view.ViewGroup.LayoutParams gridParams = this.gridView
				.getLayoutParams();
		gridParams.height = height - keyboardHeight
				- this.txtDescription.getLayoutParams().height;
		this.gridView.setLayoutParams(gridParams);
		this.gridView.setVerticalScrollBarEnabled(false);
		this.gridAdapter = new GameGridAdapter(this, factory, this.width,
				this.height);
		this.gridView.setAdapter(this.gridAdapter);

		this.keyboardView = (KeyboardView) findViewById(R.id.keyboard);
		this.keyboardView.setDelegate(this);
		android.view.ViewGroup.LayoutParams KeyboardParams = this.keyboardView
				.getLayoutParams();
		KeyboardParams.height = keyboardHeight;
		this.keyboardView.setLayoutParams(KeyboardParams);

		this.keyboardOverlay = (TextView) findViewById(R.id.keyboard_overlay);

	}

	private List<Word> createListWord(MatchData match) {

		List<ItemData> items = match.getItems();
		Collections.sort(items);

		ArrayList<Word> words = new ArrayList<Word>();
		int order = 0;
		for (ItemData item : items) {

			Word word = new Word(order, new Position(), item);
			words.add(word);
			order++;
		}

		return words;

	}

	@Override
	public void setDraft(boolean isDraft) {

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		int x = (int) event.getX();
		int y = (int) event.getY();
		int itemPosition = gridView.pointToPosition(x, y);

		switch (event.getAction()) {

		case MotionEvent.ACTION_DOWN: {

			View child = gridView.getChildAt(itemPosition);

			if (child == null
					|| child.getTag().equals(GameGridAdapter.AREA_BLOCK)) {

				this.dowIsPlayable = false;
				return true;
			}

			boolean hDir = false;
			boolean vDir = false;

			hDir = checkHashLeftOrChild(itemPosition);
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
			this.txtDescription.setText(title);

			this.gridAdapter.notifyDataSetChanged();

			break;
		}
		}
		return true;
	}

	@Override
	public void onKeyDown(String value, int[] location, int width) {

		// Deplace l'overlay du clavier
		if (value.equals(" ") == false) {
			int offsetX = (this.keyboardOverlay.getWidth() - width) / 2;
			int offsetY = (int) TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP,
					AppInitial.KEYBOARD_OVERLAY_OFFSET, getResources()
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

		if (tmp >= 1 && tmp <= this.width * this.height
				&& !gridAdapter.isBlock(tmp)) {

			view.setBackgroundResource(R.drawable.area_selected);
			view = gridView.getChildAt(tmp);
			view.setBackgroundResource(R.drawable.area_current);
			previousPosition = tmp;

		}

		this.gridAdapter.notifyDataSetChanged();

	}

	private int moveItemSelected(String value) {

		int tmp = -1;
		if (previousPosition != -1) {

			if (currentDir != -1) {

				if (value.equals(" ")) {

					tmp = this.currentDir == 0 ? previousPosition - this.width
							: previousPosition - 1;
				} else {

					tmp = this.currentDir == 0 ? previousPosition + 1
							: previousPosition + this.width;
				}

			}
		}

		return tmp;

	}

	private void setCurrentDir(int currentDir) {
		this.currentDir = currentDir;
	}

	private void resetViewSelected(int position) {

		if (currentDir == 0) {

			if (position / this.width == previousPosition / this.width) {
				view.setBackgroundResource(R.drawable.area_selected);
			} else {
				view.setBackgroundResource(R.drawable.area_empty);
			}

		} else if (currentDir == 1) {

			if (position % this.width == previousPosition % this.width) {
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
					int begin = p.getY() + width * p.getX();
					View v = gridView.getChildAt(begin + i);
					v.setBackgroundResource(R.drawable.area_selected);
					currentArea.add(v);
				}

			} else if (currentDir == 1) {

				for (int i = 0; i < currentWord.getItem().getAnswer().length(); i++) {

					Position p = currentWord.getPosition();
					int begin = p.getY() + width * p.getX();
					View v = gridView.getChildAt(begin + i * width);
					v.setBackgroundResource(R.drawable.area_selected);
					currentArea.add(v);
				}
			}

		}

	}

	private void clearLineClicked() {

		for (View v : currentArea) {

			v.setBackgroundResource(R.drawable.area_empty);
		}

		currentArea.removeAll(currentArea);

	}

	private Word getCurrentWord(int currentDir, int position) {

		if (currentDir == 0) {

			for (Word w : entries) {

				Position p = w.getPosition();

				int lenght = w.getItem().getAnswer().length();

				if (p.getDir() == 0) {

					int beginPosition = p.getY() + p.getX() * this.width;
					int endPosition = beginPosition + lenght - 1;

					if (position >= beginPosition && position <= endPosition) {

						return w;
					}

				}
			}

		} else if (currentDir == 1) {

			for (Word w : entries) {
				Position p = w.getPosition();

				int lenght = w.getItem().getAnswer().length();

				if (p.getDir() == 1) {
					int beginPosition = p.getY() + p.getX() * this.width;
					int endPosition = beginPosition + this.width
							* (p.getX() + lenght - 1);

					if (position >= beginPosition && position <= endPosition) {

						int mod1 = beginPosition % this.width;
						int mod2 = position % this.width;

						if (mod1 == mod2)
							return w;
					}
				}

			}
		}

		return null;
	}

	private boolean checkHashLeftOrChild(int position) {

		View leftChild = this.gridView.getChildAt(position - 1);
		View rightChild = this.gridView.getChildAt(position + 1);

		if (leftChild != null || rightChild != null) {

			if (leftChild != null)
				if (leftChild.getTag().equals(GameGridAdapter.AREA_WRITABLE)) {
					return true;
				}

			if (rightChild != null) {
				if (rightChild.getTag().equals(GameGridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkHashUnderOrUpChild(int position) {

		View upChild = this.gridView.getChildAt(position - width);
		View underChild = this.gridView.getChildAt(position + width);
		if (upChild != null || underChild != null) {
			if (upChild != null) {
				if (upChild.getTag().equals(GameGridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}

			if (underChild != null) {
				if (underChild.getTag().equals(GameGridAdapter.AREA_WRITABLE)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.play_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.action_check) {

			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("Do you want to finish ?");
			dialog.setCancelable(false);
			dialog.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {

							currentMode = GRID_MODE.CHECK;
							List<Integer> positions = getListPositionWrong();
							showError(positions);
							gridAdapter.notifyDataSetChanged();
							MatchProcess process = new MatchProcess();
							process.sendRequestUpdateScore(m.getId(), 70, 15);
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
			;

		}

		return super.onOptionsItemSelected(item);
	}

	private List<Integer> getListPositionWrong() {

		ArrayList<Integer> positions = new ArrayList<Integer>();

		String[][] answer = this.gridAdapter.getAnswer();

		String[][] correctAnswer = factory.getBoard();

		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {

				if (answer[i][j] != null) {
					if (!answer[i][j].equalsIgnoreCase(correctAnswer[i][j])) {

						int p = i * this.width + j;
						positions.add(p);
					}
				}

			}
		}

		return positions;
	}

	private void showError(List<Integer> positions) {

		for (Integer i : positions) {

			TextView tv = (TextView) this.gridView.getChildAt(i);

			if (tv != null) {
				tv.setTextColor(this.getResources().getColor(R.color.wrong));
			}
		}
	}

	private int getTimeMinutes(double end) {

		double time = (end - this.initialTime) / 60;

		return (int) time;
	}

}
