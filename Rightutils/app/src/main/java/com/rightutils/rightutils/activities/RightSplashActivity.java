package com.rightutils.rightutils.activities;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Anton Maniskevich on 22.02.14.
 */
public abstract class RightSplashActivity extends Activity implements RightSplash {

	private int splashDelay = 2000;
	private boolean doInit = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			doInit();
		} else {
			doInit = savedInstanceState.getBoolean(Boolean.class.getName(), true);
			splashDelay = savedInstanceState.getInt(Integer.class.getName(), splashDelay);
			if (doInit) {
				doInit();
			}
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(Boolean.class.getName(), doInit);
		outState.putInt(Integer.class.getName(), splashDelay);
	}

	private void doInit() {
		doInit = false;
		final Handler handler = new Handler();
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				doStart();
				RightSplashActivity.this.finish();
			}
		}, splashDelay);
	}

	@Override
	public void onBackPressed() {
	}

	public void setSplashDelay(int splashDelay) {
		this.splashDelay = splashDelay;
	}
}
