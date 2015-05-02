package org.com.myapp.adapter;

import java.util.List;

import org.com.myapp.activity.R;
import org.com.myapp.model.SubjectData;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class GridSubjectAdapter extends BaseAdapter {

	private Activity activity;
	private LayoutInflater inflater;
	List<SubjectData> dataList;

	public GridSubjectAdapter(Activity act, List<SubjectData> datas) {
		this.activity = act;
		this.dataList = datas;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return dataList.size();
	}

	@Override
	public Object getItem(int position) {

		return dataList.get(position);
	}

	@Override
	public long getItemId(int position) {

		return dataList.get(position).getIdSubject();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (inflater == null) {
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}

		if (convertView == null) {
			convertView = inflater.inflate(R.layout.grid_subject_item, null);

		}

		TextView tvSubject = (TextView) convertView
				.findViewById(R.id.tvSubject);

		SubjectData subjectData = dataList.get(position);
		tvSubject.setText(subjectData.getName());

		return convertView;
	}

}
