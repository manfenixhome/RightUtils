package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Anton Maniskevich on 8/11/14.
 */
public class TypefaceTextView extends TextView {

	private String fontName;

	public TypefaceTextView(Context context) {
		super(context, null);
	}

	public TypefaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}
		fontName = TypefaceUtils.setFont(context, attrs, this, android.R.attr.textViewStyle);
	}

	public String getFontName() {
		return fontName;
	}

	//TODO set font programmatically
}
