package com.rightutils.example.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;

import com.rightutils.example.R;
import com.rightutils.example.loaders.CustomLoader;
import com.rightutils.rightutils.loaders.BaseLoader;
import com.rightutils.rightutils.loaders.BaseLoaderListener;

/**
 * Created by Anton Maniskevich on 3/31/15.
 */
public class LoaderActivity extends ActionBarActivity implements View.OnClickListener {

	private static final String TAG = LoaderActivity.class.getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loader);
		findViewById(R.id.btn_start_loader).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_start_loader:
				new CustomLoader(this)
						.setCancelable(false)
						.setLoaderListener(new BaseLoaderListener<Boolean>() {
							@Override
							public void onLoadFinished(FragmentActivity activity, Fragment fragmentContainer, Boolean data, BaseLoader<Boolean> loader) {
								Log.i(TAG, "Activity = " + activity);
								Log.i(TAG, "Fragment = " + fragmentContainer);
								Log.i(TAG, "Result = " + data);
							}
						})
						.execute();
				break;
		}
	}
}
