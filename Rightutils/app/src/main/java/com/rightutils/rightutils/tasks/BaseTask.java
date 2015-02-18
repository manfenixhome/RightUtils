package com.rightutils.rightutils.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public abstract class BaseTask extends AsyncTask<String, Integer, Boolean> {

	public interface Callback {
		public void successful();

		public void failed();
	}

	protected Context context;
	protected View progressBar;
	protected Callback callback;

	public BaseTask setCallback(Callback callback) {
		this.callback = callback;
		return this;
	}


	public BaseTask(Context context, View progressBar) {
		this.context = context;
		this.progressBar = progressBar;
	}

	@Override
	protected void onPreExecute() {
		if (progressBar != null) {
			progressBar.setOnClickListener(null);
			progressBar.setVisibility(ProgressBar.VISIBLE);
		}
		super.onPreExecute();
	}


	@Override
	protected void onPostExecute(Boolean result) {
		if (progressBar != null) {
			progressBar.setVisibility(ProgressBar.GONE);
		}
		super.onPostExecute(result);

		if (callback != null) {
			if (result) {
				callback.successful();
			} else {
				callback.failed();
			}
		}
	}

	public void setProgressBar(View progressBar) {
		this.progressBar = progressBar;
		progressBar.setVisibility(View.VISIBLE);
	}

	public void setContext(Context context) {
		this.context = context;
	}
}