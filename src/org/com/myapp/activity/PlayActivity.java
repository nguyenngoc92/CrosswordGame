package org.com.myapp.activity;

import java.util.ArrayList;

import org.com.myapp.adapter.GameGirdAdapter;
import org.com.myapp.factory.CrossWordFactory;
import org.com.myapp.keyboard.KeyboardView;
import org.com.myapp.keyboard.KeyboardViewInterface;
import org.com.myapp.model.Word;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.GridView;
import android.widget.TextView;

public class PlayActivity extends ActionBarActivity implements OnTouchListener,
		KeyboardViewInterface {

	private ArrayList<Word> words;
	private CrossWordFactory factory;

	private GridView gridView;

	private KeyboardView keyboardView;

	private GameGirdAdapter gridAdapter;
	private TextView txtDescription;
	private TextView keyboardOverlay;

	private int width;
	private int height;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_play);

		/* Match match = (Match) getIntent().getSerializableExtra("match"); */

	}

	@Override
	public void onKeyDown(String value, int[] location, int width) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onKeyUp(String string) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDraft(boolean isDraft) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		// TODO Auto-generated method stub
		return false;
	}

	public ArrayList<Word> getWords() {
		return words;
	}

	public void setWords(ArrayList<Word> words) {
		this.words = words;
	}

	public CrossWordFactory getFactory() {
		return factory;
	}

	public void setFactory(CrossWordFactory factory) {
		this.factory = factory;
	}

}
