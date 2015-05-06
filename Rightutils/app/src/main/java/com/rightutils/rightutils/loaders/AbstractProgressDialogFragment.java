package com.rightutils.rightutils.loaders;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public abstract class AbstractProgressDialogFragment<T> extends DialogFragment implements LoaderCallbacks<T>, OnCancelListener {

	private static final String TAG = AbstractProgressDialogFragment.class.getSimpleName();
	private BaseLoader<T> loader;
	protected abstract void onLoadComplete(T data);
	protected abstract void onCancelLoad();
	private final Handler handler = new Handler();
	private String message;
	private int loaderId;

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setRetainInstance(true);
		
		LoaderManager loaderManager = getLoaderManager();
		if (loaderManager.getLoader(loaderId) != null) {
			loaderManager.initLoader(loaderId, null, this);
		}else{
	        startLoading();
		}
	}

	@Override
	public ProgressDialog onCreateDialog(Bundle savedInstanceState) {
		ProgressDialog progressDialog = new ProgressDialog(getActivity());
		progressDialog.setMessage(message);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setOnCancelListener(this);
		progressDialog.show();
		return progressDialog;
	}
	
	@Override
	public void onDestroyView() {
		//DialogFragment dismissed on orientation change when setRetainInstance(true) is set (compatibility library)
		// Issue 17423: http://code.google.com/p/android/issues/detail?id=17423
		//
		if (getDialog() != null && getRetainInstance()) {
			getDialog().setOnDismissListener(null);
		}
		super.onDestroyView();
	}

	protected void startLoading() {
		getLoaderManager().initLoader(loaderId, null, this);
	} 
	
	@Override
	public Loader<T> onCreateLoader(int id, Bundle args) {
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<T> loader, T data) {
		onLoadComplete(data);
		hideDialog();
	}

	@Override
	public void onLoaderReset(Loader<T> loader) {
	}
	
	@Override
	public void onCancel(DialogInterface dialog) {
		super.onCancel(dialog);
		loader.cancelLoad();
		loader.setCanceled(true);
		onCancelLoad();
	}
	
	private void hideDialog() {
		handler.post(new Runnable() {

			@Override
			public void run() {
				dismissAllowingStateLoss();
			}
		});
	}

	public void setLoader(BaseLoader<T> loader) {
		this.loader = loader;
	}

	public BaseLoader<T> getLoader() {
		return loader;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setLoaderId(int loaderId) {
		this.loaderId = loaderId;
	}
}
