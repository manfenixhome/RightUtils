package com.rightutils.example.activities;

import android.app.ProgressDialog;
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
		findViewById(R.id.btn_default_theme).setOnClickListener(this);
		findViewById(R.id.btn_holo_dark).setOnClickListener(this);
		findViewById(R.id.btn_custom_theme).setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_default_theme:
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
			case R.id.btn_holo_dark:
				new CustomLoader(this)
						.setCancelable(false)
						.setTheme(android.R.style.Theme_Holo_Dialog)
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
			case R.id.btn_custom_theme:
				new CustomLoader(this)
						.setCancelable(false)
						.setTheme(R.style.CustomLoaderTheme)
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
