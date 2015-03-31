package com.rightutils.example.loaders;

import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.rightutils.rightutils.loaders.BaseLoader;

/**
 * Created by Anton Maniskevich on 3/31/15.
 */
public class CustomLoader extends BaseLoader<Boolean> {

	private static final String TAG = CustomLoader.class.getSimpleName();

	public CustomLoader(FragmentActivity fragmentActivity) {
		super(fragmentActivity, 1);
	}

	@Override
	public Boolean loadInBackground() {
		for (int i = 0; i < 10; i++) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Log.i(TAG, ""+i);
		}
		return false;
	}
}
