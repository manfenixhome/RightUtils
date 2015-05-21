package com.rightutils.example.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.rightutils.example.R;
import com.rightutils.example.fragments.RightSwipeRefreshFragment;
import com.rightutils.rightutils.collections.RightList;
import com.rightutils.rightutils.widgets.RightSwipeRefreshLayour;

/**
 * Created by Anton Maniskevich on 5/13/15.
 */
public class RightSwipeRefreshViewPagerActivity extends AppCompatActivity implements View.OnClickListener {

	private static final String TAG = RightSwipeRefreshViewPagerActivity.class.getSimpleName();
	private View extraView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_right_swipe_refresh_view_pager);
		findViewById(R.id.btn_show_hide).setOnClickListener(this);
		extraView = findViewById(R.id.extra_view);
		RightList<Fragment> fragments = new RightList<Fragment>();
		fragments.add(RightSwipeRefreshFragment.newInstance());
		fragments.add(RightSwipeRefreshFragment.newInstance());
		fragments.add(RightSwipeRefreshFragment.newInstance());

		SectionsPagerAdapter mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), fragments);
		ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
		viewPager.setAdapter(mSectionsPagerAdapter);
		viewPager.setOffscreenPageLimit(3);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_show_hide:
				if (extraView.getVisibility() == View.VISIBLE) {
					extraView.setVisibility(View.GONE);
				} else {
					extraView.setVisibility(View.VISIBLE);
				}
				break;
		}
	}


	private class SectionsPagerAdapter extends FragmentPagerAdapter {

		private RightList<Fragment> fragments;

		public SectionsPagerAdapter(FragmentManager fm, RightList<Fragment> fragments) {
			super(fm);
			this.fragments = fragments;
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
	}



}
