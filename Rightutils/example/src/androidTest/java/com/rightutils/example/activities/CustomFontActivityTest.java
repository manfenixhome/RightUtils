package com.rightutils.example.activities;

import android.test.ActivityInstrumentationTestCase2;

import com.rightutils.example.R;
import com.rightutils.rightutils.widgets.TypefaceTextView;

/**
 * Created by Anton Maniskevich on 2/23/15.
 */
public class CustomFontActivityTest extends ActivityInstrumentationTestCase2<CustomFontActivity> {

	private static final String TAG = CustomFontActivity.class.getSimpleName();

	private CustomFontActivity activity;

	public CustomFontActivityTest() {
		super(CustomFontActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		activity = getActivity();
	}

	public void testSetFontWithAttr() throws Exception {
		TypefaceTextView textView = (TypefaceTextView) activity.findViewById(R.id.txt_attr_font);

		assertNotNull(textView);
		assertEquals("fonts/Roboto-Light.ttf", textView.getFontName());
	}

	public void testSetFontWithStyle() throws Exception {
		TypefaceTextView textView = (TypefaceTextView) activity.findViewById(R.id.txt_style_font);

		assertNotNull(textView);
		assertEquals("fonts/Roboto-Light.ttf", textView.getFontName());
	}

	public void testSetFontWithTheme() throws Exception {
		TypefaceTextView textView = (TypefaceTextView) activity.findViewById(R.id.txt_style_in_theme);

		assertNotNull(textView);
		assertEquals("fonts/Roboto-Regular.ttf", textView.getFontName());
	}
}
