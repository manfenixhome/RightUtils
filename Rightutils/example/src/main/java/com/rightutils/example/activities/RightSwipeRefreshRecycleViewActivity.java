package com.rightutils.example.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.rightutils.example.R;
import com.rightutils.example.decorator.DividerItemDecoration;
import com.rightutils.rightutils.collections.RightList;
import com.rightutils.rightutils.widgets.RightSwipeRefreshLayour;

/**
 * Created by Anton Maniskevich on 5/13/15.
 */
public class RightSwipeRefreshRecycleViewActivity extends AppCompatActivity implements RightSwipeRefreshLayour.OnRefreshListener {

	private static final String TAG = RightSwipeRefreshRecycleViewActivity.class.getSimpleName();
	private RightSwipeRefreshLayour rightSwipeRefreshLayour;
	private SimpleAdapter adapter;
	private LinearLayoutManager mLayoutManager;
	private RecyclerView mRecyclerView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_right_swipe_refresh_recyclerview);
		mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setItemAnimator(new DefaultItemAnimator());
		mLayoutManager = new LinearLayoutManager(RightSwipeRefreshRecycleViewActivity.this);
		mRecyclerView.setLayoutManager(mLayoutManager);
		RightList<String> values = RightList.asRightList("100", "101", "102", "103", "104", "105", "106", "107", "108", "109", "110", "111", "112", "113", "114");
		adapter = new SimpleAdapter(values);
		mRecyclerView.setAdapter(adapter);
		rightSwipeRefreshLayour = (RightSwipeRefreshLayour) findViewById(R.id.refresh);
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
						int firstValue = Integer.valueOf(adapter.getItem(0));
						if (firstValue > 90) {
							adapter.addTop(RightList.asRightList(String.valueOf(firstValue-2), String.valueOf(firstValue-1)));
						}
						mLayoutManager.scrollToPositionWithOffset(0, 0);
						break;
					case RightSwipeRefreshLayour.BOTTOM_REFRESH:
						int lastValue = Integer.valueOf(adapter.getItem(adapter.getItemCount()-1));
						if (lastValue < 124) {
							adapter.addToBottom(RightList.asRightList(String.valueOf(lastValue + 1), String.valueOf(lastValue + 2)));
						}
						mLayoutManager.scrollToPositionWithOffset(mLayoutManager.findFirstCompletelyVisibleItemPosition()+1,0);
						break;
				}
				rightSwipeRefreshLayour.setRefreshing(false, refreshType);
			}
		}.execute();
	}

	private static class SimpleAdapter extends RecyclerView.Adapter<SimpleAdapter.ViewHolder> {

		private RightList<String> items;

		public SimpleAdapter(RightList<String> items) {
			this.items = items;
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycleview, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.setItem(getItem(position));
		}

		@Override
		public int getItemCount() {
			return items.size();
		}

		public String getItem(int position) {
			return items.get(position);
		}

		public void addTop(RightList<String> newItems) {
			items.addAll(0, newItems);
			notifyItemRangeInserted(0, newItems.size());
		}

		public void addToBottom(RightList<String> newItems) {
			items.addAll(newItems);
			notifyItemRangeInserted(items.size()-newItems.size(), newItems.size());
		}

		static class ViewHolder extends RecyclerView.ViewHolder {
			TextView title;
			public ViewHolder(View itemView) {
				super(itemView);
				title = (TextView) itemView.findViewById(R.id.txt_title);
			}

			public void setItem(String item) {
				title.setText(item);
			}
		}

	}
}
