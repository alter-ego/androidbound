package com.alterego.androidbound.android.converters;

import java.util.Locale;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.android.interfaces.IFontManager;
import com.alterego.androidbound.interfaces.IValueConverter;

public class FontConverter implements IValueConverter {
	
	public static final String CONVERTER_NAME = "ToFont";
	private IFontManager mFontManager;
	private IAndroidLogger mLogger = NullAndroidLogger.instance;
	
	public FontConverter (IFontManager mgr, IAndroidLogger logger) {
		mFontManager = mgr;
		mLogger  = logger;
	}

	 @Override
     public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
         mLogger.debug("ValueConverters FontForView convert, parameter = " + (String)parameter);
         return  mFontManager.getFont((String)parameter);
     }

     @Override
     public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
         return null;
     }
	
	public static String getConverterName() {
		return CONVERTER_NAME;
	}

}
