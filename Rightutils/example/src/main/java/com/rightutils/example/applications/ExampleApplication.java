package com.rightutils.example.applications;

import android.app.Application;

import com.rightutils.example.db.DBUtils;

/**
 * Created by Anton Maniskevich on 1/20/15.
 */
public class ExampleApplication extends Application {

	public static DBUtils dbUtils;

	@Override
	public void onCreate() {
		super.onCreate();
		dbUtils = DBUtils.newInstance(this, "example.sqlite", 1);
	}
}
