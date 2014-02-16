package com.alterego.androidbound.android.interfaces;

import android.graphics.Typeface;

public interface IFontManager {
	
	void setDefaultFont(Typeface font);
	
	Typeface getDefaultFont();

	void registerFont (String name, Typeface font);
	
	Typeface getFont (String name);
	
}
