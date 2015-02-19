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
public class TypefacedEditText extends EditText {
	private static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

	public TypefacedEditText(Context context, AttributeSet attrs) {
		super(context, attrs);

		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceView);
		String fontName = styledAttrs.getString(R.styleable.TypefaceView_typeface);
		styledAttrs.recycle();

		if (fontName != null) {
			setFont(context, fontName);
		} else {
			TypedArray styledAttrsTheme = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTheme);
			CharSequence[] charSequences = styledAttrsTheme.getTextArray(R.styleable.TypefaceTheme_customTypefaceStyle);
			styledAttrsTheme.recycle();
			if (charSequences != null && charSequences.length > 0) {
				setFont(context, charSequences[0].toString());
			}
		}
	}

	private void setFont(Context context, String fontName) {
		if (!fonts.containsKey(fontName)) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			setTypeface(typeface);
			fonts.put(fontName, typeface);
		} else {
			setTypeface(fonts.get(fontName));
		}
	}
}
