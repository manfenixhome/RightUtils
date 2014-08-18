package com.rightutils.rightutils.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.rightutils.rightutils.collections.Operation;
import com.rightutils.rightutils.collections.Predicate;
import com.rightutils.rightutils.collections.RightList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public abstract class RightDBUtils {

	private static final String TAG = RightDBUtils.class.getName();
	protected RightDBHandler dbHandler;
	protected SQLiteDatabase db;

	protected void setDBContext(Context context, String name, int version) {
		dbHandler = new RightDBHandler(context, name, version);
		try {
			dbHandler.createDataBase();
			if (db != null && db.isOpen()) {
			} else {
				db = dbHandler.getWritableDatabase();
			}
		} catch (IOException e) {
			Log.e(TAG, "Create DB", e);
		}
	}

	public <T> void deleteWhere(Class<T> type, String where) {
		db.delete(getTableName(type), where, null);
	}

	public int countResultsByQuery(String query) {
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			return cursor.getCount();
		}
		cursor.close();
		return 0;
	}

	public <T> RightList<T> executeQuery(String query, Class<T> type) {
		return queryListMapper(query, type);
	}

	public <T> RightList<T> getAll(Class<T> type) {
		String query = String.format("select * from %s", getTableName(type));
		return queryListMapper(query, type);
	}

	public <T> RightList<T> getAllWhere(String where, Class<T> type) {
		String query = String.format("select * from %s where %s", getTableName(type), where);
		return queryListMapper(query, type);
	}

	public <T> void add(RightList<T> elements) {
		elements.foreach(new Operation<T>() {
			@Override
			public void execute(T value) {
				add(value);
			}
		});
	}

	public <T>long add(final T element) {
		final ContentValues values = new ContentValues();
		RightList.asRightList(element.getClass().getDeclaredFields()).filter(new Predicate<Field>() {
			@Override
			public boolean apply(Field value) {
				return !value.isAnnotationPresent(ColumnIgnore.class) && !Modifier.isStatic(value.getModifiers());
			}
		}).foreach(new Operation<Field>() {
			@Override
			public void execute(Field value) {
				if (value.isAnnotationPresent(ColumnAutoInc.class)) {
					valueAutoIncMapper(values, value, element);
				} else {
					valueMapper(values, value, element);
				}
			}
		});
		return db.insert(getTableName(element.getClass()), null, values);
	}


	//INNER METHODS

	private <T> void valueAutoIncMapper(ContentValues values, Field field, T element) {
		field.setAccessible(true);
		try {
			long id = (Long) field.get(element);
			if (id > 0) {
				values.put(getColumnName(field), id);
			}
		}  catch (IllegalAccessException e) {
			Log.e(TAG, "valueAutoIncMapper", e);
		}
	}

	//TODO add real and other types
	private <T> void valueMapper(ContentValues values, Field field, T element) {
		field.setAccessible(true);
		try {
			if (field.getType().isAssignableFrom(String.class)) {
				values.put(getColumnName(field), (String) field.get(element));
			} else if (field.getType().isAssignableFrom(long.class)) {
				values.put(getColumnName(field), (Long) field.get(element));
			} else if (field.getType().isAssignableFrom(int.class)) {
				values.put(getColumnName(field), (Integer) field.get(element));
			} else if (field.getType().isAssignableFrom(boolean.class)) {
				values.put(getColumnName(field), ((Boolean) field.get(element)) ? 1 : 0);
			} else {
				Log.w(TAG, String.format("Type '%s' of field '%s' not supported.", field.getType().toString(), field.getName()));
			}
		} catch (IllegalAccessException e) {
			Log.e(TAG, "valueMapper", e);
		}
	}

	private <T> RightList<T> queryListMapper(String query, Class<T> type) {
		RightList<T> results = new RightList<T>();
		Cursor cursor = db.rawQuery(query, null);
		if (cursor.moveToFirst()) {
			do {
				results.add(cursorMapper(cursor, type));
			} while (cursor.moveToNext());
		}
		cursor.close();
		return results;
	}

	private <T> T cursorMapper(Cursor cursor, Class<T> type) {
		T result = null;
		try {
			result = type.newInstance();
			for (Field field : result.getClass().getDeclaredFields()) {
				if (!field.isAnnotationPresent(ColumnIgnore.class)) {
					if (!Modifier.isStatic(field.getModifiers())) {
						fieldMapper(result, cursor, field, getColumnName(field));
					}
				}
			}
		} catch (Exception e) {
			Log.e(TAG, "cursorMapper", e);
		}
		return result;
	}

	//TODO add real and other types
	private <T> void fieldMapper(T result, Cursor cursor, Field field, String columnName) {
		field.setAccessible(true);
		try {
			if (field.getType().isAssignableFrom(String.class)) {
				field.set(result, cursor.getString(cursor.getColumnIndex(columnName)));
			} else if (field.getType().isAssignableFrom(long.class)) {
				field.set(result, cursor.getLong(cursor.getColumnIndex(columnName)));
			} else if (field.getType().isAssignableFrom(int.class)) {
				field.set(result, cursor.getInt(cursor.getColumnIndex(columnName)));
			} else if (field.getType().isAssignableFrom(boolean.class)) {
				field.set(result, cursor.getLong(cursor.getColumnIndex(columnName)) == 1);
			} else {
				Log.w(TAG, String.format("Type '%s' of field '%s' not supported.", field.getType().toString(), field.getName()));
			}
		} catch (IllegalAccessException e) {
			Log.e(TAG, "fieldMapper", e);
		}
	}

	private <T> String getTableName(Class<T> type) {
		String tableName = type.getSimpleName();
		if (type.isAnnotationPresent(TableName.class)) {
			tableName = type.getAnnotation(TableName.class).value();
		}
		return tableName;
	}

	private <T> String getColumnName(Field field) {
		String columnName = field.getName();
		if (field.isAnnotationPresent(ColumnName.class)) {
			columnName = field.getAnnotation(ColumnName.class).value();
		}
		return columnName;
	}

}
