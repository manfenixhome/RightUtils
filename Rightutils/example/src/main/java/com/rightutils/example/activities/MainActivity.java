package com.rightutils.example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;

import com.rightutils.example.R;

/**
 * Created by Anton Maniskevich on 3/31/15.
 */
public class MainActivity extends ActionBarActivity implements View.OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findViewById(R.id.btn_custom_font_activity).setOnClickListener(this);
		findViewById(R.id.btn_loader_activity).setOnClickListener(this);
		findViewById(R.id.btn_fragment_loader).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_custom_font_activity:
				startActivity(new Intent(MainActivity.this, CustomFontActivity.class));
				break;
			case R.id.btn_loader_activity:
				startActivity(new Intent(MainActivity.this, LoaderActivity.class));
				break;
			case R.id.btn_fragment_loader:
				startActivity(new Intent(MainActivity.this, FragmentLoaderActivity.class));
				break;
		}
	}
}
