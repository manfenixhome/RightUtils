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
public class TypefacedRadioButton extends RadioButton {
	private static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

	public TypefacedRadioButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefacedTextView);
		String fontName = styledAttrs.getString(R.styleable.TypefacedTextView_typeface);
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
}
