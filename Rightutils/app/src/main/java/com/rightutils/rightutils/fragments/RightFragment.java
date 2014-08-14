package com.rightutils.rightutils.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Created by Anton Maniskevich on 03.03.14.
 */
public abstract class RightFragment extends Fragment {

	private static final String TAG = RightFragment.class.getSimpleName();
	protected Context context;
	private Fragment childFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = getActivity();
		if (savedInstanceState != null) {
			for (Field field : ((Object) childFragment).getClass().getDeclaredFields()) {
				if (field.isAnnotationPresent(ExtraStore.class)) {
					field.setAccessible(true);
					try {
						if (Serializable.class.isAssignableFrom(field.getType())) {
							field.set(childFragment, savedInstanceState.getSerializable(field.getName()));
						}
					} catch (IllegalAccessException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		for (Field field : ((Object) childFragment).getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(ExtraStore.class)) {
				try {
					field.setAccessible(true);
					if (Serializable.class.isAssignableFrom(field.getType())) {
						outState.putSerializable(field.getName(), (Serializable) field.get(childFragment));
					} else {
						Log.e(TAG, field.getName() + " is not Serializable!");
					}
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		}
	}

	protected void setChildFragment(Fragment childFragment) {
		this.childFragment = childFragment;
	}
}
