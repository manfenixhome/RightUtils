package com.rightutils.rightutils.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.rightutils.rightutils.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Anton Maniskevich on 2/20/15.
 */
public class TypefaceUtils {

	private static Map<String, Typeface> fonts = new HashMap<String, Typeface>();

	public static <T extends TextView> void setFont(T object, Context context, String fontName) {
		Typeface typeface;
		if (!fonts.containsKey(fontName)) {
			typeface = Typeface.createFromAsset(context.getAssets(), fontName);
			fonts.put(fontName, typeface);
		} else {
			typeface = fonts.get(fontName);
		}
		object.setTypeface(typeface);
	}

	public static <T extends TextView> void setFont(Context context, AttributeSet attrs, T object, int themeStyleAttr) {
		TypedArray styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.TypefaceView);
		String fontName = styledAttrs.getString(R.styleable.TypefaceView_typeface);
		styledAttrs.recycle();
		if (fontName != null) {
			setFont(object, context, fontName);
		} else {
			TypedArray styledAttrsThemeStyle = context.obtainStyledAttributes(attrs, new int[] {themeStyleAttr});
			int resourceId = styledAttrsThemeStyle.getResourceId(0,-1);
			if (resourceId != -1) {
				fontName = context.obtainStyledAttributes(resourceId, R.styleable.TypefaceView).getString(R.styleable.TypefaceView_typeface);
			}
			if (fontName != null) {
				setFont(object, context, fontName);
			} else {
				TypedArray styledAttrsTheme = context.obtainStyledAttributes(attrs, R.styleable.TypefaceTheme);
				CharSequence[] charSequences = styledAttrsTheme.getTextArray(R.styleable.TypefaceTheme_customTypefaceStyle);
				styledAttrsTheme.recycle();
				if (charSequences != null && charSequences.length > 0) {
					setFont(object, context, charSequences[0].toString());
				}
			}
		}
	}
}
