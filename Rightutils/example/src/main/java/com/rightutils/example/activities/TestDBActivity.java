package com.rightutils.example.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.rightutils.example.R;
import com.rightutils.example.applications.ExampleApplication;
import com.rightutils.example.entities.Company;

/**
 * Created by Anton Maniskevich on 1/20/15.
 */
public class TestDBActivity extends Activity {

	private static final String TAG = TestDBActivity.class.getSimpleName();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_db_test);
		ExampleApplication.dbUtils.add(new Company(1, "Company 1"));
		Log.i(TAG, ExampleApplication.dbUtils.getAll(Company.class).convertToString());
	}
}
