package com.rightutils.rightutils.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by Anton Maniskevich on 8/18/14.
 */
public class RightUtils {

	private static final String TAG = RightUtils.class.getSimpleName();

	public static boolean isOnline(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	public static void hideKeyboard(Context context) {
		InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
		View view = ((Activity) context).getCurrentFocus();
		if (view != null) {
			inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	public static void showKeyHash(Context context, String packageName) {
		PackageInfo info;
		try {
			info = context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				String something = new String(Base64.encode(md.digest(), 0));
				Log.e(TAG, "hash key=" + something);
			}
		} catch (PackageManager.NameNotFoundException e1) {
			Log.e(TAG, "name not found", e1);
		} catch (NoSuchAlgorithmException e) {
			Log.e(TAG, "no such an algorithm");
		} catch (Exception e) {
			Log.e(TAG, "exception", e);
		}
	}
	//TODO decodeFile

	private RightUtils() {
	}
}
