package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

import com.rightutils.rightutils.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by eKreative on 6/10/14.
 */
public class TypefaceAutoScrollTextView extends TextView {

	public TypefaceAutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypefaceUtils.setFont(context, attrs, this, android.R.attr.textViewStyle);
	}

	@Override
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
		if (focused) {
			super.onFocusChanged(focused, direction, previouslyFocusedRect);
		}
	}

	@Override
	public void onWindowFocusChanged(boolean focused) {
		if (focused) {
			super.onWindowFocusChanged(focused);
		}
	}

	@Override
	public boolean isFocused() {
		return true;
	}
}
