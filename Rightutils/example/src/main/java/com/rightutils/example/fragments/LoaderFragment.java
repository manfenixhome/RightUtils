package com.rightutils.example.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.rightutils.example.R;
import com.rightutils.example.loaders.CustomLoader;
import com.rightutils.rightutils.loaders.BaseLoader;
import com.rightutils.rightutils.loaders.BaseLoaderListener;

/**
 * Created by Anton Maniskevich on 3/31/15.
 */
public class LoaderFragment extends Fragment implements View.OnClickListener {

	private static final String TAG = LoaderFragment.class.getSimpleName();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_loader, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		view.findViewById(R.id.btn_default_theme).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_default_theme:
				new CustomLoader(getActivity())
						.setCancelable(false)
						.setContainer(LoaderFragment.class)
						.setLoaderListener(new BaseLoaderListener<Boolean>() {
							@Override
							public void onLoadFinished(FragmentActivity activity, Fragment fragmentContainer, Boolean data, BaseLoader<Boolean> loader) {
								Log.i(TAG, "Activity = " + activity);
								Log.i(TAG, "Fragment = " + fragmentContainer);
								Log.i(TAG, "Result = " + data);
							}
						})
						.execute();
				break;
		}
	}
}
