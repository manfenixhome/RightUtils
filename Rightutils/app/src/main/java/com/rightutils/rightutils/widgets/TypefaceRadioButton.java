package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.RadioButton;

import com.rightutils.rightutils.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton Maniskevich on 8/11/14.
 */
public class TypefaceRadioButton extends RadioButton {

	public TypefaceRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypefaceUtils.setFont(context, attrs, this, android.R.attr.radioButtonStyle);
	}

}
