package com.rightutils.rightutils.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by Anton Maniskevich on 12/5/14.
 */
public class SupportRightActionBarActivity extends ActionBarActivity {

	private int fragmentContainer;
	private Fragment initFragment;
	protected Context context;

	protected void initActivity(int fragmentContainer, Fragment initFragment) {
		this.fragmentContainer = fragmentContainer;
		this.initFragment = initFragment;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this;
		if (savedInstanceState == null) {
			pushFragment(initFragment);
		}
	}

	public void pushFragment(Fragment fragment) {
		String backStateName = ((Object) fragment).getClass().getName();
		FragmentManager manager = getSupportFragmentManager();
		boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

		if (!fragmentPopped) {
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(fragmentContainer, fragment, backStateName);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	public Fragment getLastFragment() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		String tag = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 1).getName();
		return getSupportFragmentManager().findFragmentByTag(tag);
	}
}
