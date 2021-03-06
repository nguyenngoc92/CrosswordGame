package org.com.myapp.adapter;

import java.util.List;

import org.com.myapp.activity.R;
import org.com.myapp.model.UserData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class MatchRankListAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	private List<UserData> userDatas;

	public MatchRankListAdapter(Activity activity, List<UserData> userDatas) {
		this.activity = activity;
		this.userDatas = userDatas;
	}

	@Override
	public int getCount() {

		return userDatas.size();
	}

	@Override
	public Object getItem(int position) {

		return userDatas.get(position);
	}

	@Override
	public long getItemId(int position) {

		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null) {
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_match_rank, null);
		}

		TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
		TextView tvScore = (TextView) convertView.findViewById(R.id.tvScore);
		TextView tvRank = (TextView) convertView.findViewById(R.id.tvRank);
		TextView tvTime = (TextView) convertView.findViewById(R.id.tvTime);
		/*
		 * Set image url afterward
		 */

		UserData userData = userDatas.get(position);
		tvName.setText(userData.getUsername());
		tvScore.setText(userData.getScore() + "");
		tvRank.setText(userData.getRank() + "");
		tvTime.setText(userData.getTime()+"");

		return convertView;
	}

}
