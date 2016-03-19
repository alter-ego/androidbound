package solutions.alterego.androidbound.android.converters;

import android.view.View;

import java.util.Locale;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public class BooleanToVisibilityConverter implements IValueConverter {

    public static final String CONVERTER_NAME = "ToVisibility";

    public static final String PARAMETER_INVERT_TAG = "invert";

    public static String getConverterName() {
        return CONVERTER_NAME;
    }

    @Override
    public Object convert(Object value, Class<?> targetType, Object parameter, Locale culture) {
        boolean result = false;

        if (value instanceof Boolean) {
            result = ((Boolean) value);
        } else if (value != null) {
            result = true;
        }

        if (parameter != null && (parameter instanceof String) && ((String) parameter).equalsIgnoreCase(PARAMETER_INVERT_TAG)) {
            result = !result;
        }

        return result ? View.VISIBLE : View.GONE;
    }

    @Override
    public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
        return IBinding.noValue;
    }

}
