package solutions.alterego.androidbound.android.converters;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import android.annotation.SuppressLint;
import android.view.View;

import java.util.Locale;

import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public class BooleanToInvisibilityConverterTest {

    private IValueConverter mTestConverter;

    public BooleanToInvisibilityConverterTest() {
        mTestConverter = new BooleanToInvisibilityConverter();
    }

    @Test
    public void converterHasAName() {
        Assertions.assertThat(mTestConverter.getBindingName()).isNotNull();
        Assertions.assertThat(mTestConverter.getBindingName()).isEqualTo(BooleanToInvisibilityConverter.CONVERTER_NAME);
    }

    @Test
    public void converterDoesntUseTargetClass() {
        Assertions.assertThat(mTestConverter.convert(false, null, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, Boolean.class, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, String.class, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, Integer.class, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, Long.class, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, Double.class, null, null)).isEqualTo(View.INVISIBLE);
    }

    @SuppressLint("NewApi")
    @Test
    public void converterDoesntUseLocale() {
        Assertions.assertThat(mTestConverter.convert(false, null, null, null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, null, Locale.UK)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, null, Locale.US)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, null, Locale.CHINA)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, null, Locale.forLanguageTag("ar-001"))).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void converterInterpretsNullValuesAsFalse() {
        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void converterInterpretsNonNullValuesAsTrue() {
        Assertions.assertThat(mTestConverter.convert("test", null, null, null)).isEqualTo(View.VISIBLE);
        Assertions.assertThat(mTestConverter.convert('c', null, null, null)).isEqualTo(View.VISIBLE);
        Assertions.assertThat(mTestConverter.convert(0, null, null, null)).isEqualTo(View.VISIBLE);
        Assertions.assertThat(mTestConverter.convert(1, null, null, null)).isEqualTo(View.VISIBLE);
        Assertions.assertThat(mTestConverter.convert(mTestConverter, null, null, null)).isEqualTo(View.VISIBLE);
    }

    @Test
    public void convertsBooleanToVisibility() {
        Assertions.assertThat(mTestConverter.convert(true, null, null, null)).isEqualTo(View.VISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, null, null)).isEqualTo(View.INVISIBLE);
    }

    @Test
    public void parameterInvertInverts() {
        Assertions.assertThat(mTestConverter.convert(true, null, "invert", null)).isEqualTo(View.INVISIBLE);
        Assertions.assertThat(mTestConverter.convert(false, null, "invert", null)).isEqualTo(View.VISIBLE);

        Assertions.assertThat(mTestConverter.convert(true, null, "invert", null))
                .isNotEqualTo(mTestConverter.convert(true, null, null, null));
        Assertions.assertThat(mTestConverter.convert(false, null, "invert", null))
                .isNotEqualTo(mTestConverter.convert(false, null, null, null));
    }

    @Test
    public void converterDoesntConvertBack() {
        Assertions.assertThat(mTestConverter.convertBack(true, null, null, null)).isEqualTo(IBinding.noValue);
    }

}