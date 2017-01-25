package solutions.alterego.androidbound.android.converters;

import java.util.Locale;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.interfaces.IFontManager;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.ILogger;

public class FontConverter implements IValueConverter {

    public static final String CONVERTER_NAME = "ToFont";

    private IFontManager mFontManager;

    private ILogger mLogger = NullLogger.instance;

    public FontConverter(IFontManager mgr, ILogger logger) {
        mFontManager = mgr;
        mLogger = logger;
    }

    public static String getConverterName() {
        return CONVERTER_NAME;
    }

    @Override
    public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
        mLogger.debug("ToFont convert, parameter = " + (String) parameter);
        return mFontManager.getFont((String) parameter);
    }

    @Override
    public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
        return null;
    }

}
