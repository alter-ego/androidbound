package com.alterego.androidbound.interfaces;

import java.util.Locale;

public interface IValueConverter {
	Object convert(Object value, Class<?> targetType, Object parameter, Locale culture);
	Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture); 
}
