package com.rightutils.rightutils.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

/**
 * Created by Anton Maniskevich on 22.02.14.
 */
public abstract class RightFragmentActivity extends ActionBarActivity {
	private int fragmentContainer;
	private boolean messageForExit = true;
	private Fragment initFragment;
	protected Context context;
	private boolean exitApp = false;

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
			ft.replace(fragmentContainer, fragment);
			ft.addToBackStack(backStateName);
			ft.commit();
		}
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
			if (messageForExit) {
				if (!exitApp) {
					Toast.makeText(this, "Tap one more for exit app", Toast.LENGTH_SHORT).show();
					exitApp = true;
					new TimeOutTask().execute();
				} else {
					finish();
				}
			} else {
				finish();
			}
		} else {
			super.onBackPressed();
		}
	}

	protected void disableMessageForExit() {
		messageForExit = false;
	}

	private class TimeOutTask extends AsyncTask<Void, Void, Boolean> {

		@Override
		protected Boolean doInBackground(Void... params) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			exitApp = false;
			return true;
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
