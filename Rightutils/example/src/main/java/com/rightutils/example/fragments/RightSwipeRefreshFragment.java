package com.rightutils.example.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rightutils.example.R;
import com.rightutils.rightutils.collections.RightList;
import com.rightutils.rightutils.widgets.RightSwipeRefreshLayour;

/**
 * Created by Anton Maniskevich on 5/20/15.
 */
public class RightSwipeRefreshFragment extends Fragment implements RightSwipeRefreshLayour.OnRefreshListener {

	private static final String TAG = RightSwipeRefreshFragment.class.getSimpleName();
	private RightSwipeRefreshLayour rightSwipeRefreshLayour;
	private ArrayAdapter adapter;

	public static RightSwipeRefreshFragment newInstance() {
		return new RightSwipeRefreshFragment();
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_right_swipe_refresh, container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		RightList<String> values = RightList.asRightList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114");
		adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, values);
		((ListView)view.findViewById(R.id.list_view)).setAdapter(adapter);
		rightSwipeRefreshLayour = (RightSwipeRefreshLayour) view.findViewById(R.id.refresh);
		rightSwipeRefreshLayour.setOnRefreshListener(this);
	}

	@Override
	public void onRefresh(final @RightSwipeRefreshLayour.RefreshType int refreshType) {
		rightSwipeRefreshLayour.setRefreshing(true, refreshType);
		new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void aVoid) {
				switch (refreshType) {
					case RightSwipeRefreshLayour.TOP_REFRESH:
						int firstValue = Integer.valueOf((String) adapter.getItem(0));
						if (firstValue > 90) {
							adapter.insert(String.valueOf(--firstValue), 0);
							adapter.insert(String.valueOf(--firstValue), 0);
						}
						break;
					case RightSwipeRefreshLayour.BOTTOM_REFRESH:
						int lastValue = Integer.valueOf((String) adapter.getItem(adapter.getCount()-1));
						Log.i(TAG, "lastValue=" + lastValue);
						if (lastValue < 124) {
							adapter.add(String.valueOf(++lastValue));
							adapter.add(String.valueOf(++lastValue));
						}
						break;
				}
				adapter.notifyDataSetChanged();
				rightSwipeRefreshLayour.setRefreshing(false, refreshType);
			}
		}.execute();
	}
}
