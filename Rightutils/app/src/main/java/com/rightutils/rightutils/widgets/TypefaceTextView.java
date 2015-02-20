package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Anton Maniskevich on 8/11/14.
 */
public class TypefaceTextView extends TextView {

	public TypefaceTextView(Context context) {
		super(context, null);
	}

	public TypefaceTextView(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}
		TypefaceUtils.setFont(context, attrs, this, android.R.attr.textViewStyle);
	}

}
