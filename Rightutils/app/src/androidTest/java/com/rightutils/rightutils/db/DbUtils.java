package com.rightutils.rightutils.db;

import android.content.Context;

/**
 * Created by Anton Maniskevich on 2/23/15.
 */
public class DBUtils extends RightDBUtils {

	public static DBUtils newInstance(Context context, String name, int version) {
		DBUtils dbUtils = new DBUtils();
		dbUtils.setDBContext(context, name, version);
		return dbUtils;
	}
}
