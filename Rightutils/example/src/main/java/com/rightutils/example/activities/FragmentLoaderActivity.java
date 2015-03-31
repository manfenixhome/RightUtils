package com.rightutils.example.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.rightutils.example.R;
import com.rightutils.example.fragments.LoaderFragment;

/**
 * Created by Anton Maniskevich on 3/31/15.
 */
public class FragmentLoaderActivity extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment_loader);
		if (savedInstanceState == null) {
			String backStackTag = LoaderFragment.class.getName();
			getSupportFragmentManager().beginTransaction()
					.add(R.id.fragment_container, new LoaderFragment(), backStackTag)
					.addToBackStack(backStackTag)
					.commit();
		}
	}
}
