package com.alterego.androidbound.android;

import java.util.Locale;

import android.view.View;

import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.IValueConverter;

public class BooleanToVisibilityConverter implements IValueConverter {

	@Override
	public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
		if(value instanceof Boolean)
			return ((Boolean)value) ? View.VISIBLE : View.GONE;
		return value == null ? View.GONE : View.VISIBLE;
	}

	@Override
	public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
		return IBinding.noValue;
	}

}
