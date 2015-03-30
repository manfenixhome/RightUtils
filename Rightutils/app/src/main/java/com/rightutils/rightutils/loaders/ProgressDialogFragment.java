package com.rightutils.rightutils.loaders;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.UUID;

public class ProgressDialogFragment<T> extends AbstractProgressDialogFragment<T> {

	private LoaderListener<T> taskLoaderListener;
	private final Handler handler = new Handler();
	public static final String TAG_FRAGMENT = UUID.randomUUID().toString();

	private static <T> ProgressDialogFragment newInstance(BaseLoader<T> loader, String message, int loaderId) {
		ProgressDialogFragment<T> fragment = new ProgressDialogFragment<T>();
		fragment.setLoader(loader);
		fragment.setMessage(message);
		fragment.setLoaderId(loaderId);
		return fragment;
	}

	protected void setTaskLoaderListener(LoaderListener<T> taskLoaderListener) {
		this.taskLoaderListener = taskLoaderListener;
	}

	@Override
	protected void onLoadComplete(final T data) {
		if (taskLoaderListener != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (getLoader().getContainer() != null) {
						taskLoaderListener.onLoadFinished(getActivity(), getFragmentByTag(getActivity(), getLoader().getContainer().getName()), data);
					} else {
						taskLoaderListener.onLoadFinished(getActivity(), null, data);
					}
				}
			});
		}
	}

	@Override
	protected void onCancelLoad() {
		if (taskLoaderListener != null) {
			handler.post(new Runnable() {

				@Override
				public void run() {
					taskLoaderListener.onCancelLoad();

				}
			});
		}
	}

	private static Fragment getFragmentByTag(FragmentActivity fragmentActivity, String tag) {
		if (fragmentActivity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		return fragmentActivity.getSupportFragmentManager().findFragmentByTag(tag);
	}

	public static class Builder<T> {
		FragmentActivity fa;
		BaseLoader<T> loader;
		LoaderListener<T> taskLoaderListener;
		Boolean cancelable;
		String progressMsg;
		int loaderId;

		public Builder(FragmentActivity fragmentActivity, BaseLoader<T> loader, String progressMsg, int loaderId) {
			this.fa = fragmentActivity;
			this.loader = loader;
			this.progressMsg = progressMsg;
			this.loaderId = loaderId;
		}

		public Builder<T> setTaskLoaderListener(LoaderListener<T> taskLoaderListener) {
			this.taskLoaderListener = taskLoaderListener;
			return this;
		}

		public Builder<T> setCancelable(Boolean cancelable) {
			this.cancelable = cancelable;
			return this;
		}

		public void show() {
			//remove prev if exists
			FragmentManager fm = fa.getSupportFragmentManager();

			FragmentTransaction ft = fm.beginTransaction();
			Fragment prev = fm.findFragmentByTag(TAG_FRAGMENT);
			if (prev != null) {
				ft.remove(prev);
			}

			ProgressDialogFragment fragment = newInstance(loader, progressMsg, loaderId);
			fragment.setTaskLoaderListener(taskLoaderListener);
			fragment.setCancelable(cancelable);

			fragment.show(ft, TAG_FRAGMENT);

		}
	}

}
