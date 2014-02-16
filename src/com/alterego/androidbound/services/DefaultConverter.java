package com.alterego.androidbound.services;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.alterego.androidbound.interfaces.IValueConverter;

import android.util.Log;

public class DefaultConverter implements IValueConverter {
	private static final Map<String, Method> converters = new HashMap<String, Method>();
	public static final DefaultConverter instance = new DefaultConverter();

	static {
		Method[] methods = DefaultConverter.class.getDeclaredMethods();
		for (Method method : methods) {
			if (method.getParameterTypes().length == 1) {				
				converters.put(method.getParameterTypes()[0].getName() + "_"
						+ method.getReturnType().getName(), method); 
			}
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> Object convert(Object from, Class<T> to) {
		if (from == null)
			return null;
		
		if (to.isAssignableFrom(from.getClass())) 
			return from; //to.cast(from);

		String converterId = from.getClass().getName() + "_" + to.getName();
		Method converter = converters.get(converterId);
		if (converter == null) 
			throw new UnsupportedOperationException("Cannot convert from "
					+ from.getClass().getName() + " to " + to.getName()
					+ ". Requested converter does not exist.");

		try {
			return converter.invoke(to, from); //return to.cast();
		} catch (Exception e) {
			throw new RuntimeException("Cannot convert from "
					+ from.getClass().getName() + " to " + to.getName()
					+ ". Conversion failed with " + e.getMessage(), e);
		}
	}

	public static boolean unbox(Boolean val) {
		return val;
	}
	
	public static int unbox(Integer val) {
		return val;
	}
	
	public static long unbox(Long val) {
		return val;
	}
	
	public static float unbox(Float val) {
		return val;
	}
	
	public static double unbox(Double val) {
		return val;
	}
	
	public static char unbox(Character val) {
		return val;
	}
	
	public static byte unbox(Byte val) {
		return val;
	}

	public static short unbox(Short val) {
		return val;
	}
	
	public static Boolean box(boolean val) {
		return val;
	}
	
	public static Integer box(int val) {
		return val;
	}
	
	public static Long box(long val) {
		return val;
	}
	
	public static Float box(float val) {
		return val;
	}
	
	public static Double box(double val) {
		return val;
	}
	
	public static Character box(char val) {
		return val;
	}
	
	public static Byte box(byte val) {
		return val;
	}
	
	public static Short box(short val) {
		return val;
	}
	
	public static Boolean integerToBoolean(Integer value) {
		return value.intValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

	public static Integer booleanToInteger(Boolean value) {
		return value.booleanValue() ? Integer.valueOf(1) : Integer.valueOf(0);
	}
	
	public static Boolean doubleToBoolean(Double value) {
		return value.doubleValue() == 0 ? Boolean.FALSE : Boolean.TRUE;
	}

	public static Double booleanToDouble(Boolean value) {
		return value.booleanValue() ? Double.valueOf(1) : Double.valueOf(0);
	}

	public static BigDecimal doubleToBigDecimal(Double value) {
		return new BigDecimal(value.doubleValue());
	}

	public static Double bigDecimalToDouble(BigDecimal value) {
		return Double.valueOf(value.doubleValue());
	}

	public static String integerToString(Integer value) {
		return value.toString();
	}

	public static Integer stringToInteger(String value) {
		return Integer.valueOf(value);
	}

	public static String booleanToString(Boolean value) {
		return value.toString();
	}
	
	public static CharSequence integerToCharSequence(Integer value) {
		return integerToString(value);
	}
	
	public static Integer charSequenceToInteger(CharSequence value) {
		return stringToInteger(value.toString());
	}

	public static Boolean stringToBoolean(String value) {
		return Boolean.valueOf(value);
	}
	
	public static String doubleToString(Double value) {
		return value.toString();
	}
	
	public static Double stringToDouble(String value) {
		return Double.valueOf(value);
	}
	
	public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
		return convert(value, targetType);
	}

	public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
		return convert(value, targetType);
	}
}
