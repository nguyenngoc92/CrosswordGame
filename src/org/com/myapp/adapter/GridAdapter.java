package org.com.myapp.adapter;

import java.util.HashMap;

import org.com.myapp.activity.GameActivity;
import org.com.myapp.activity.GameActivity.GRID_MODE;
import org.com.myapp.activity.R;
import org.com.myapp.model.Cell;

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

public class GridAdapter extends BaseAdapter {

	public static final int AREA_BLOCK = -1;
	public static final int AREA_WRITABLE = 0;
	private Context context;

	@SuppressLint("UseSparseArrays")
	private HashMap<Integer, TextView> views = new HashMap<Integer, TextView>();

	private int displayHeight;
	private int size;

	private String[][] answer;
	private String[][] correctAnswer;

	private Cell[][] grid;

	@SuppressWarnings("deprecation")
	public GridAdapter(Activity act, Cell[][] grid) {

		this.context = (Context) act;
		this.grid = grid;

		this.initialData(grid);

		// Calcul area height
		Display display = act.getWindowManager().getDefaultDisplay();
		this.displayHeight = display.getWidth() / this.size;

	}

	private void initialData(Cell[][] grid) {
		this.size = grid.length;

		correctAnswer = new String[size][size];
		answer = new String[size][size];

		for (int r = 0; r < size; r++) {
			for (int c = 0; c < size; c++) {
				correctAnswer[r][c] = grid[r][c].getLetter();

				if (grid[r][c].getLetter() != null)
					answer[r][c] = " ";

			}
		}

	}

	@Override
	public int getCount() {

		return this.size * this.size;
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
		int r = (int) (position / this.size);
		int c = (int) (position % this.size);

		String data = answer[r][c];
		String correcData = correctAnswer[r][c];
		Cell cell = grid[r][c];

		if (v == null) {

			v = new TextView(context);
			v.setLayoutParams(new GridView.LayoutParams(
					GridView.LayoutParams.FILL_PARENT, this.displayHeight));
			v.setTextSize((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4 ? 30
					: 20);
			v.setGravity(Gravity.CENTER);

			if (data != null) {
				v.setBackgroundResource(R.drawable.area_empty);
				v.setTag(this.AREA_WRITABLE);
				
				if(cell.getCellNode() != null && cell.getCellNode().isStartOfWord()){
					
				}
			} else {
				v.setBackgroundResource(R.drawable.area_block);
				v.setTag(this.AREA_BLOCK);
			}

		}
		this.views.put(position, v);

		if (((GameActivity) context).currentMode == GRID_MODE.CHECK) {

		} else if (((GameActivity) context).currentMode == GRID_MODE.SOLVE) {

		}

		return v;
	}

	public boolean isBlock(int position) {

		int r = position / this.size;
		int c = position % this.size;

		return (this.answer[r][c] == null);

	}

	public void setValue(int position, String value) {

		int r = position / this.size;
		int c = position % this.size;

		if (this.answer[r][c] != null)
			answer[r][c] = value;
	}

}
