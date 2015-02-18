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
public class TypefacedAutoScrollTextView extends TextView {

	private static Map<String, Typeface> fonts = new HashMap<String, Typeface>();


	public TypefacedAutoScrollTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceView);
		String fontName = styledAttrs.getString(R.styleable.TypefaceView_typeface);
		styledAttrs.recycle();

		if (fontName != null) {
			if (!fonts.containsKey(fontName)) {
				Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
				setTypeface(typeface);
				fonts.put(fontName, typeface);
			} else {
				setTypeface(fonts.get(fontName));
			}
		}
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
