package com.rightutils.example.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;

import com.rightutils.example.R;
import com.rightutils.rightutils.widgets.RightSwipeRefreshLayour;

/**
 * Created by Anton Maniskevich on 5/13/15.
 */
public class RightSwipeRefreshActivity extends AppCompatActivity implements RightSwipeRefreshLayour.OnRefreshListener {

	private static final String TAG = RightSwipeRefreshActivity.class.getSimpleName();
	private RightSwipeRefreshLayour rightSwipeRefreshLayour;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_right_swipe_refresh);
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
				rightSwipeRefreshLayour.setRefreshing(false, refreshType);
			}
		}.execute();
	}
}
