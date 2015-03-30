package com.rightutils.rightutils.loaders;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

public interface LoaderListener<T> {

	void onLoadFinished(FragmentActivity activity, Fragment fragmentContainer, T data);

	void onCancelLoad();
}
