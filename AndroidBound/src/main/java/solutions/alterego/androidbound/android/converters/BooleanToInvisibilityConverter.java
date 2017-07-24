package solutions.alterego.androidbound.android.converters;

import android.view.View;

import java.util.Locale;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

//behaves similarly to BooleanToVisibilityConverter, but the views are INVISIBLE instead of GONE, so true means VISIBLE and false INVISIBLE
public class BooleanToInvisibilityConverter implements IValueConverter {

    public static final String CONVERTER_NAME = "ToInvisibility";

    public static final String PARAMETER_INVERT_TAG = "invert";

    @Override
    public String getBindingName() {
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

        return result ? View.VISIBLE : View.INVISIBLE;
    }

    @Override
    public Object convertBack(Object value, Class<?> targetType, Object parameter, Locale culture) {
        return IBinding.noValue;
    }

}
