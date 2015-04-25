package org.com.myapp.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.com.myapp.AppConfig;
import org.com.myapp.adapter.GridAdapter;
import org.com.myapp.factory.CrossWordFactory;
import org.com.myapp.keyboard.KeyboardView;
import org.com.myapp.keyboard.KeyboardViewInterface;
import org.com.myapp.model.Cell;
import org.com.myapp.model.Direction;
import org.com.myapp.model.ItemData;
import org.com.myapp.model.MatchData;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Toast;

public class GameActivity extends ActionBarActivity implements
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);

		m = (MatchData) getIntent().getSerializableExtra("match");

		words.addAll(this.createWordList(m));
		this.initial();

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

	private void initial() {

		this.grid = (GridView) findViewById(R.id.grid);
		this.tvDescription = (TextView) findViewById(R.id.tvDescription);
		this.keyboardView = (KeyboardView) findViewById(R.id.keyboard);
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

		} else {
			System.out
					.println("==========================================================");
			Toast t = Toast.makeText(this, "Error create grid for crossword",
					Toast.LENGTH_LONG);
			t.show();
			this.finish();
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.game_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
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
							List<Integer> positions = getListPositionWrong();
							showError(positions);
							gridAdapter.notifyDataSetChanged();

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

		if (id == R.id.action_getRankByMatch) {

			Intent intent = new Intent(getApplicationContext(),
					MatchRankActivity.class);
			intent.putExtra("ID", m.getId());
			startActivity(intent);

		}
		return super.onOptionsItemSelected(item);
	}

	protected void showError(List<Integer> positions) {

	}

	protected List<Integer> getListPositionWrong() {

		return null;
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

	private void checkAnswer() {

	}

	private void solveAnswer() {

	}

}
