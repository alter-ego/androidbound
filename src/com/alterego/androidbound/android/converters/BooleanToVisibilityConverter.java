package com.alterego.androidbound.android.converters;

import java.util.Locale;

import android.view.View;

import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.IValueConverter;

public class BooleanToVisibilityConverter implements IValueConverter {
	
	public static final String CONVERTER_NAME = "ToVisibility";
	public static final String PARAMETER_INVERT_TAG = "invert";
	
	@Override
	public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
		boolean result = false;
		
		if(value instanceof Boolean)
			result = ((Boolean)value);
		else if (value!=null)
			result = true;
		
		if (parameter!=null && (parameter instanceof String) && ((String)parameter).equalsIgnoreCase(PARAMETER_INVERT_TAG))
			result = !result;
		
		return result ? View.VISIBLE : View.GONE;
	}

	@Override
	public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
		return IBinding.noValue;
	}
	
	public static String getConverterName() {
		return CONVERTER_NAME;
	}

}
