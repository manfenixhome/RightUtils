package com.rightutils.rightutils.db;

import android.content.Context;

/**
 * Created by Fenix-mobile on 12.03.2015.
 */
public class DBUtilsNew extends RightDBUtils {
	public static DBUtilsNew newInstance(Context context, String name, int version) {
		DBUtilsNew dbUtils = new DBUtilsNew();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}
}
