package com.rightutils.rightutils.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.ProgressBar;

public abstract class BaseTask extends AsyncTask<String, Integer, Boolean> {

	protected Context context;
	protected View progressBar;

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
			progressBar.setVisibility(ProgressBar.INVISIBLE);
		}
		super.onPostExecute(result);
	}

	public void setProgressBar(View progressBar) {
		this.progressBar = progressBar;
		progressBar.setVisibility(View.VISIBLE);
	}

	public void setContext(Context context) {
		this.context = context;
	}
}