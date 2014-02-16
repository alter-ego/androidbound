package com.alterego.androidbound.binds;

import com.alterego.androidbound.interfaces.IValueConverter;

public class BindingSpecification {
	public String Target;
	public String Path;
	public IValueConverter Converter;
	public Object ConverterParameter;
	public BindingMode Mode;
	public Object FallbackValue;
}