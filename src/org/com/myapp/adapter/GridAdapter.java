package org.com.myapp.adapter;

import java.util.HashMap;

import org.com.myapp.activity.R;
import org.com.myapp.model.Cell;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	public final static int AREA_BLOCK = -1;
	public final static int AREA_WRITABLE = 0;
	private Context context;

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, TextView> views = new HashMap<Integer, TextView>();

	private int displayHeight;

	private String[][] answer;
	private String[][] correctAnswer;

	private Cell[][] grid;
	private int rows;
	private int cols;

	int index = 0;

	@SuppressWarnings("deprecation")
	public GridAdapter(Activity act, Cell[][] grid) {

		this.context = (Context) act;
		this.grid = grid;
		rows = grid.length;
		cols = grid[0].length;
		this.initialData(grid);

		// Calcul area height
		Display display = act.getWindowManager().getDefaultDisplay();
		this.displayHeight = display.getWidth() / this.rows;

	}

	private void initialData(Cell[][] grid) {

		correctAnswer = new String[rows][cols];
		answer = new String[rows][cols];

		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {
				correctAnswer[r][c] = grid[r][c].getLetter();

				if (grid[r][c].getLetter() != null)
					answer[r][c] = " ";

			}
		}

	}

	@Override
	public int getCount() {

		return this.rows * this.cols;
	}

	@Override
	public Object getItem(int position) {

		return views.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		TextView v = this.views.get(position);
		int r = (int) (position / this.cols);
		int c = (int) (position % this.cols);

		String data = answer[r][c];
		Cell cell = grid[r][c];

		if (v == null) {

			v = new TextView(context);
			v.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.FILL_PARENT, this.displayHeight));
			v.setTextSize((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4 ? 30
					: 20);
			v.setGravity(Gravity.CENTER);

			if (data != null) {
				// v.setBackgroundResource(R.drawable.area_empty);
				v.setBackgroundResource(R.color.cell_empty);
				v.setTag(AREA_WRITABLE);

				if (cell.getCellNode() != null
						&& cell.getCellNode().isStartOfWord()) {
					index++;

					int id = getBackGroundId(index);
					if (id != 0) {
						
						v.setBackgroundResource(id);
						v.setBackgroundColor(Color.parseColor("#ffffff"));
					}
				}
			} else {
				// v.setBackgroundResource(R.drawable.area_block);
				v.setBackgroundResource(R.color.cell);
				v.setTag(AREA_BLOCK);
			}

		}
		this.views.put(position, v);


		return v;
	}

	private int getBackGroundId(int index) {

		switch (index) {
		case 1:
			return R.drawable.ic_1;
		case 2:
			return R.drawable.ic_2;

		case 3:
			return R.drawable.ic_3;

		case 4:
			return R.drawable.ic_4;

		case 5:
			return R.drawable.ic_5;

		case 6:
			return R.drawable.ic_6;

		case 7:
			return R.drawable.ic_7;

		case 8:
			return R.drawable.ic_8;
		case 9:
			return R.drawable.ic_9;

		case 10:
			return R.drawable.ic_10;

		case 11:
			return R.drawable.ic_11;

		case 12:
			return R.drawable.ic_12;

		case 13:
			return R.drawable.ic_13;

		case 14:
			return R.drawable.ic_14;

		case 15:
			return R.drawable.ic_15;

		default:
			return 0;
		}

	}

	public boolean isBlock(int position) {

		int r = position / this.cols;
		int c = position % this.cols;

		return (this.answer[r][c] == null);

	}

	public void setValue(int position, String value) {

		int r = position / this.cols;
		int c = position % this.cols;

		if (this.answer[r][c] != null)
			answer[r][c] = value;
	}

	public String[][] getAnswer() {
		return answer;
	}

	public String[][] getCorrectAnswer() {
		return correctAnswer;
	}

	public void printlnAnswer() {
		String str = "";
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < cols; c++) {

				if (answer[r][c] == null)
					str += "*";
				else {
					str += answer[r][c];
				}
				if (c == cols - 1)
					str += "\n";

			}
		}

		System.out.println(str);
	}

}
