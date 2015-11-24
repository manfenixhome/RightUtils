package com.rightutils.rightutils.db;

import com.github.andreyrage.leftdb.LeftDBUtils;
import com.rightutils.rightutils.collections.Operation;
import com.rightutils.rightutils.collections.RightList;

public abstract class RightDBUtils extends LeftDBUtils {

	public <T> RightList<T> executeQuery(String query, Class<T> type) {
		return RightList.asRightList(super.executeQuery(query, type));
	}

	public <T> RightList<T> getAll(Class<T> type) {
		return RightList.asRightList(super.getAll(type));
	}

	public <T> RightList<T> getAllLimited(Class<T> type, long limit) {
		return RightList.asRightList(super.getAllLimited(type, limit));
	}

	public <T> RightList<T> getAllWhere(String where, Class<T> type) {
		return RightList.asRightList(super.getAllWhere(where, type));
	}

	public <T> void add(RightList<T> elements) {
		elements.foreach(new Operation<T>() {
			@Override
			public void execute(T value) {
				add(value);
			}
		});
	}
}
