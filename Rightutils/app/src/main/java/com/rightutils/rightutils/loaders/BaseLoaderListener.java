package com.rightutils.rightutils.loaders;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

/**
 * Created by Anton Maniskevich on 3/26/15.
 */
public abstract class BaseLoaderListener<T> implements LoaderListener<T> {

	@Override
	public void onLoadFinished(FragmentActivity activity, Fragment fragmentContainer, T data, BaseLoader<T> loader) {
	}

	@Override
	public void onCancelLoad() {
	}
}
