package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import com.rightutils.rightutils.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton Maniskevich on 8/11/14.
 */
public class TypefaceEditText extends EditText {

	public TypefaceEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypefaceUtils.setFont(context, attrs, this, android.R.attr.editTextStyle);
	}

}
