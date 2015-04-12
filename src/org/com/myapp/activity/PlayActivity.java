package org.com.myapp.activity;

import java.util.ArrayList;

import org.com.myapp.AppInitial;
import org.com.myapp.adapter.GameGirdAdapter;
import org.com.myapp.factory.CrossWordFactory;
import org.com.myapp.keyboard.KeyboardView;
import org.com.myapp.keyboard.KeyboardViewInterface;
import org.com.myapp.model.Position;
import org.com.myapp.model.Word;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Display;
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

	private ArrayList<Word> words;
	private ArrayList<View> selectedArea = new ArrayList<View>();
	private CrossWordFactory factory;

	private GridView gridView;

	private KeyboardView keyboardView;

	private GameGirdAdapter gridAdapter;
	private TextView txtDescription;
	private TextView keyboardOverlay;

	private int width = 13;
	private int height = 13;

	private int currentX;
	private int currentY;

	private boolean downIsPlayable;
	private int downPos;
	private int downX;
	private int downY;
	private int currentPos;

	private boolean horizontal;

	private Word currentWord;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		factory = new CrossWordFactory(width, height);
		words = AppInitial.getWords();

		for (Word w : words) {

			w.setPosition(factory.addWord(w.getItem().getAnswer()));

			System.out.println(w.getItem().getAnswer() + " "
					+ w.getPosition().toString());

		}

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

		this.gridAdapter = new GameGirdAdapter(this, factory, this.width,
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

	@Override
	public void onKeyDown(String value, int[] location, int width) {
		System.out.println("onKeyDown: " + value + ", insert in: " + currentX
				+ "x" + currentY);

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
		System.out.println("onKeyUp: " + value + ", insert in: " + currentX
				+ "x" + currentY);

		if (value.equals(" ") == false) {
			this.keyboardOverlay.setAnimation(AnimationUtils.loadAnimation(
					this, R.anim.keyboard_overlay_fade_out));
			this.keyboardOverlay.setVisibility(View.INVISIBLE);
		}

		if (this.currentWord == null)
			return;

		int x = this.currentX;
		int y = this.currentY;

		if (this.gridAdapter.isBlock(x, y))
			return;

		this.gridAdapter.setValue(x, y, value);
		this.gridAdapter.notifyDataSetChanged();

		if (value.equals(" ")) {
			x = (this.horizontal ? x - 1 : x);
			y = (this.horizontal ? y : y - 1);
		} else {
			x = (this.horizontal ? x + 1 : x);
			y = (this.horizontal ? y : y + 1);
		}

		if (x >= 0 && x < this.width && y >= 0 && y < this.height
				&& this.gridAdapter.isBlock(x, y) == false) {
			this.gridView.getChildAt(y * this.width + x).setBackgroundResource(
					R.drawable.area_current);
			this.gridView
					.getChildAt(this.currentY * this.width + this.currentX)
					.setBackgroundResource(R.drawable.area_selected);
			this.currentX = x;
			this.currentY = y;
		}

	}

	@Override
	public void setDraft(boolean isDraft) {
		this.gridAdapter.setDraft(isDraft);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getActionMasked()) {

		case MotionEvent.ACTION_DOWN: {
			int position = gridView.pointToPosition((int) event.getX(),
					(int) event.getY());
			System.out.println("Position: " + position);

			View child = gridView.getChildAt(position);

			if (child == null
					|| child.getTag().equals(GameGirdAdapter.AREA_BLOCK)) {

				clearSelection();
				this.gridAdapter.notifyDataSetChanged();

				// //////////////////////////////////////////
				this.downIsPlayable = false;
				return true;
			}
			this.downIsPlayable = true;
			this.downPos = position;
			this.downX = this.downPos % this.width;
			// this.currentY = this.currentPos / this.width;

			System.out.println("ACTION_DOWN, x:" + this.downX + ", y:"
					+ this.downY + ", position: " + this.downPos);
			clearSelection();
			child.setBackgroundResource(R.drawable.area_selected);

			selectedArea.add(child);
			this.gridAdapter.notifyDataSetChanged();

			break;
		}
		case MotionEvent.ACTION_UP: {
			if (downIsPlayable == false)
				return true;
			int position = this.gridView.pointToPosition((int) event.getX(),
					(int) event.getY());
			int x = position % width;
			int y = position / width;

			System.out.println("ACTION_DOWN, x:" + x + ", y:" + y
					+ ", position: " + position);

			if (downPos == position && this.currentPos == position) {
				this.horizontal = !this.horizontal;
			} else if (this.downPos != position) {
				this.horizontal = (Math.abs(this.downX - x) > Math
						.abs(this.downY - y));

			}

			this.currentWord = getWord(downX, downY, this.horizontal);

			if (this.currentWord == null)
				break;

			if (currentWord.getPosition().getDir() == 0)
				this.horizontal = true;
			else
				this.horizontal = false;

			if (this.downPos == position) {
				this.currentX = this.downX;
				this.currentY = this.downY;
				this.currentPos = position;
			} else {
				this.currentX = this.currentWord.getPosition().getX();
				this.currentY = this.currentWord.getPosition().getY();
				this.currentPos = this.currentY * this.width + this.currentX;
			}

			this.txtDescription.setText(this.currentWord.getItem()
					.getQuestion());
			// set background color

			int dir = this.currentWord.getPosition().getDir();

			for (int l = 0; l < this.currentWord.getItem().getAnswer().length(); l++) {

				int index = this.currentWord.getPosition().getY() * this.width
						+ this.currentWord.getPosition().getX()
						+ (l * (dir == 0 ? 1 : this.width));

				View currentChild = this.gridView.getChildAt(index);

				if (currentChild != null) {
					currentChild
							.setBackgroundResource(index == this.currentPos ? R.drawable.area_current
									: R.drawable.area_selected);
					selectedArea.add(currentChild);
				}
			}

			this.gridAdapter.notifyDataSetChanged();

			break;
		}
		}
		return true;
	}

	private Word getWord(int x, int y, boolean horizontal) {

		Word horizontalWord = null;
		Word verticalWord = null;

		for (Word word : words) {

			Position p = word.getPosition();
			if (x >= p.getX() && x <= word.getMaxX()) {

				if (y >= p.getY() && y <= word.getMaxY()) {
					if (p.getDir() == 0) {
						horizontalWord = word;
					} else {
						verticalWord = word;
					}
				}
			}

		}

		if (horizontal) {
			return (horizontalWord != null) ? horizontalWord : verticalWord;
		} else {
			return (verticalWord != null) ? verticalWord : horizontalWord;
		}

	}

	private void clearSelection() {
		for (View selected : selectedArea)
			selected.setBackgroundResource(R.drawable.area_empty);
		selectedArea.clear();
	}

}
