package com.rightutils.rightutils.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RightDBHandler extends SQLiteOpenHelper {

	private static final String TAG = RightDBHandler.class.getName();
	private SQLiteDatabase dataBase;
	private Context context;
	private String name;
	private String path;
	private int version;

	private OnVersionChangeCallback mCallback;

	public RightDBHandler(Context context, String name, int version, OnVersionChangeCallback mCallback) {
		super(context, name, null, version);
		this.context = context;
		this.name = name;
		path = context.getFilesDir() + "/databases/";
		this.version = version;
		this.mCallback = mCallback;
	}

	public void createDataBase() throws IOException {
		if (!checkDataBase()) {
			Log.i(TAG, "creating db");
			copyDataBase();
		}
	}

	public void deleteDataBase() {
		if (checkDataBase()) {
			close();
			File dbFile = new File(path + name);
			dbFile.delete();
		}
	}

	private boolean checkDataBase() {
		File dbFile = new File(path + name);
		return dbFile.exists();
	}

	private void copyDataBase() throws IOException {
		InputStream myInput = context.getAssets().open(name);

		final File dir = new File(path);
		dir.mkdirs();
		final File file = new File(dir, name);

		OutputStream myOutput = new FileOutputStream(file);
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer)) > 0) {
			myOutput.write(buffer, 0, length);
		}
		myOutput.flush();
		myOutput.close();
		myInput.close();
	}

	public SQLiteDatabase openDataBase(int openType) throws SQLException {
		String myPath = path + name;
		dataBase = SQLiteDatabase.openDatabase(myPath, null, openType);
		dataBase.execSQL("PRAGMA foreign_keys=ON;");
		validateVersion();
		return dataBase;
	}

	private void validateVersion() {
		int currentVersion = dataBase.getVersion();
		if (currentVersion != version) {
			if (mCallback != null) {
				if (dataBase.isReadOnly()) {
					Log.e(TAG, "Can't upgrade read-only database from version " +
							currentVersion + " to " + version);
				}
				if (currentVersion > version) {
					mCallback.onDowngrade(dataBase, currentVersion, version);
				} else if (currentVersion < version) {
					mCallback.onUpgrade(dataBase, currentVersion, version);
				}
				dataBase.setVersion(version);
			} else {
				Log.e(TAG, "Can't upgrade database from version " + currentVersion
						+ " to " + version + ", cause onVersionChangeCallback is null");
			}
		}
	}

	@Override
	public synchronized void close() {
		if (dataBase != null)
			dataBase.close();
		super.close();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

	@Override
	public SQLiteDatabase getWritableDatabase() {
		return openDataBase(SQLiteDatabase.OPEN_READWRITE);
	}

	@Override
	public SQLiteDatabase getReadableDatabase() {
		return openDataBase(SQLiteDatabase.OPEN_READONLY);
	}

	public interface OnVersionChangeCallback {
		void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
		void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion);
	}
}