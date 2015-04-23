package org.com.myapp.adapter;

import java.util.HashMap;

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

public class GameGridAdapter /*extends BaseAdapter*/ {
	/*public static final int AREA_BLOCK = -1;
	public static final int AREA_WRITABLE = 0;
	private Context context;

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, TextView> views = new HashMap<Integer, TextView>();

	private int displayHeight;
	private int width;
	private int height;

	private String[][] answer;
	private String[][] correctAnswer;

	@SuppressWarnings("deprecation")
	public GameGridAdapter(Activity act, CrossWordFactory factory, int width,
			int height) {
		this.context = (Context) act;
		this.answer = factory.getTmp();
		this.correctAnswer = factory.getBoard();
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

		return null;
	}

	@Override
	public long getItemId(int position) {

		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView v = this.views.get(position);

		int y = (int) (position / this.width);
		int x = (int) (position % this.width);

		String data = this.answer[y][x];
		String answerData = this.correctAnswer[y][x];

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

		return v;
	}

	public boolean isBlock(int position) {
		int y = position / this.width;
		int x = position % this.width;

		return (this.answer[y][x] == null);

	}

	public void setValue(int position, String value) {
		int y = position / this.width;
		int x = position % this.width;
		if (this.answer[y][x] != null) {
			answer[y][x] = value;

		}
	}

	public void sysOut() {
		String result = "";
		for (int i = 0; i < this.width; i++) {
			for (int j = 0; j < this.height; j++) {
				result += answer[i][j] != null ? answer[i][j] : "*";
			}

			if (i < this.width - 1)
				result += "\n";
		}

		System.out.println(result);

	}

	public void setLower(boolean isLower) {
	}

	public String[][] getAnswer() {
		return answer;
	}
*/
}
