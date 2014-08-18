package com.rightutils.rightutils.activities;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

/**
 * Created by Anton Maniskevich on 22.02.14.
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public abstract class RightFragmentActivityNew extends Activity {
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
		FragmentManager manager = getFragmentManager();
		boolean fragmentPopped = manager.popBackStackImmediate(backStateName, 0);

		if (!fragmentPopped) {
			FragmentTransaction ft = manager.beginTransaction();
			ft.replace(fragmentContainer, fragment);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
	}

	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 1) {
			finish();
		} else {
			super.onBackPressed();
		}
	}

	public Fragment getLastFragment() {
		if (getFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		String tag = getFragmentManager().getBackStackEntryAt(getFragmentManager().getBackStackEntryCount() - 1).getName();
		return getFragmentManager().findFragmentByTag(tag);
	}

}
