package com.rightutils.rightutils.loaders;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.AsyncTaskLoader;

public abstract class BaseLoader<T> extends AsyncTaskLoader<T> {

	public static final String DEFAULT_MESSAGE = "Please wait...";
	private boolean canceled = false;
	private boolean cancelable = true;
	private FragmentActivity fragmentActivity;
	private LoaderListener<T> loaderListener;
	private String message = DEFAULT_MESSAGE;
	private final int loaderId;
	private Class<? extends Fragment> container;

	public BaseLoader(FragmentActivity fragmentActivity, int loaderId) {
		super(fragmentActivity);
		this.fragmentActivity = fragmentActivity;
		this.loaderId = loaderId;
	}

	public void execute() {
		Fragment lastFragment = getLastFragment(fragmentActivity);
		if (lastFragment == null || lastFragment.getLoaderManager().getLoader(loaderId) == null) {

			new ProgressDialogFragment.Builder<T>(fragmentActivity, this, message, loaderId)
					.setCancelable(cancelable)
					.setTaskLoaderListener(loaderListener)
					.show();
		}
	}

	public void setCanceled(boolean canceled) {
		this.canceled = canceled;
	}

	public boolean isCanceled() {
		return canceled;
	}

	private static Fragment getLastFragment(FragmentActivity fragmentActivity) {
		if (fragmentActivity.getSupportFragmentManager().getBackStackEntryCount() == 0) {
			return null;
		}
		return fragmentActivity.getSupportFragmentManager().findFragmentByTag(ProgressDialogFragment.TAG_FRAGMENT);
	}

	public BaseLoader<T> setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public LoaderListener<T> getLoaderListener() {
		return loaderListener;
	}

	public BaseLoader<T> setLoaderListener(LoaderListener<T> loaderListener) {
		this.loaderListener = loaderListener;
		return this;
	}

	public boolean isCancelable() {
		return cancelable;
	}

	public BaseLoader<T> setCancelable(boolean cancelable) {
		this.cancelable = cancelable;
		return this;
	}

	public Class<? extends Fragment> getContainer() {
		return container;
	}

	public BaseLoader<T> setContainer(Class<? extends Fragment> container) {
		this.container = container;
		return this;
	}
}
