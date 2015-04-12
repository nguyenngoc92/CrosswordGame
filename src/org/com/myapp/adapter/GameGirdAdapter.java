package org.com.myapp.adapter;

import java.util.HashMap;

import org.com.myapp.activity.PlayActivity;
import org.com.myapp.activity.R;
import org.com.myapp.factory.CrossWordFactory;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GameGirdAdapter extends BaseAdapter {
	public static final int AREA_BLOCK = -1;
	public static final int AREA_WRITABLE = 0;
	private Context context;

	private int displayHeight;
	private int width;
	private int height;

	private String[][] board;
	private String[][] answer;

	private boolean isDraft;
	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, TextView> views = new HashMap<Integer, TextView>();

	@SuppressWarnings("deprecation")
	public GameGirdAdapter(Activity act, CrossWordFactory factory, int width,
			int height) {
		this.context = (Context) act;
		this.board = factory.getBoard();
		this.answer = factory.getTmp();
		this.width = width;
		this.height = height;

		// Calcul area height
		Display display = act.getWindowManager().getDefaultDisplay();
		this.displayHeight = display.getWidth() / this.width;
	}

	@Override
	public int getCount() {
		return this.height * this.width;
	}

	@Override
	public Object getItem(int position) {

		return views.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView v = this.views.get(position);

		int y = (int) (position / this.width);
		int x = (int) (position % this.width);

		String data = this.answer[y][x];

		if (v == null) {

			v = new TextView(context);
			v.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.MATCH_PARENT, this.displayHeight));
			v.setTextSize((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4 ? 30
					: 20);
			v.setGravity(Gravity.CENTER);

			if (data != null) {
				v.setBackgroundResource(R.drawable.area_empty);
				v.setTag(AREA_WRITABLE);
				v.setText(data);
			} else {
				v.setBackgroundResource(R.drawable.area_block);
				v.setTag(AREA_BLOCK);
			}

			this.views.put(position, v);

		}

		if (((PlayActivity) context).currentMode == PlayActivity.GRID_MODE.NORMAL) {

			if (data != null) {
				if (Character.isLowerCase(data.charAt(0)))
					v.setTextColor(context.getResources().getColor(
							R.color.draft));
				else
					v.setTextColor(context.getResources().getColor(
							R.color.normal));

				v.setText(data);
			}
		}

		return v;
	}

	public boolean isBlock(int x, int y) {
		return (this.answer[y][x] == null);
	}

	public void setValue(int x, int y, String value) {
		if (this.answer[y][x] != null) {
			answer[y][x] = value;
		}
	}

	public void setDraft(boolean isDraft) {
		this.isDraft = isDraft;
	}

}
