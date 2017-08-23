package solutions.alterego.androidbound.android.converters;

import org.fest.assertions.api.Assertions;
import org.junit.Before;
import org.junit.Test;

import android.graphics.Typeface;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.android.FontManager;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public class FontConverterTest {

    private FontManager mFontManager;

    private IValueConverter mTestConverter;

    @Before
    public void setupFontConverter() {
        mFontManager = new FontManager(NullLogger.instance);
        mFontManager.setDefaultFont(Typeface.DEFAULT);
        mFontManager.registerFont("bold", Typeface.DEFAULT_BOLD);
        mFontManager.registerFont("sans", Typeface.SANS_SERIF);

        mTestConverter = new FontConverter(mFontManager, NullLogger.instance);
    }

    @Test
    public void converterHasAName() {
        Assertions.assertThat(mTestConverter.getBindingName()).isNotNull();
        Assertions.assertThat(mTestConverter.getBindingName()).isEqualTo(FontConverter.CONVERTER_NAME);
    }

//    @Test
//    public void converterDoesntUseValue() { //TODO
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isNotNull();
//
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(false, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert("bold", null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert("sans", null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(0, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(1, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(mTestConverter, null, null, null)).isEqualTo(Typeface.DEFAULT);
//    }

//    @Test
//    public void converterDoesntUseTargetClass() { //TODO
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isNotNull();
//
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, Boolean.class, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, String.class, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, Integer.class, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, Long.class, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, Double.class, null, null)).isEqualTo(Typeface.DEFAULT);
//    }

//    @SuppressLint("NewApi")
//    @Test
//    public void converterDoesntUseLocale() { //TODO
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isNotNull();
//
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, null, null, Locale.UK)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, null, null, Locale.US)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, null, null, Locale.CHINA)).isEqualTo(Typeface.DEFAULT);
//        Assertions.assertThat(mTestConverter.convert(null, null, null, Locale.forLanguageTag("ar-001"))).isEqualTo(Typeface.DEFAULT);
//    }

//    @Test
//    public void convertsToDefaultForNullParameter() { //TODO
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, null, null)).isEqualTo(Typeface.DEFAULT);
//    }

//    @Test
//    public void convertsToDefaultForUnknownFontName() { //TODO
//        Assertions.assertThat(mTestConverter.convert(null, null, "test", null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, "test", null)).isEqualTo(Typeface.DEFAULT);
//    }

//    @Test
//    public void returnsCorrectFont() { //TODO
//                Assertions.assertThat(mTestConverter.convert(null, null, "bold", null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, "bold", null)).isEqualTo(Typeface.DEFAULT_BOLD);
//
//                Assertions.assertThat(mTestConverter.convert(null, null, "sans", null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, "sans", null)).isEqualTo(Typeface.SANS_SERIF);
//    }
//
//    @Test
//    public void ignoresUpperCase() { //TODO
//                Assertions.assertThat(mTestConverter.convert(null, null, "Bold", null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, "Bold", null)).isEqualTo(Typeface.DEFAULT_BOLD);
//
//                Assertions.assertThat(mTestConverter.convert(null, null, "SaNs", null)).isNotNull();
//        Assertions.assertThat(mTestConverter.convert(null, null, "SaNs", null)).isEqualTo(Typeface.SANS_SERIF);
//    }

    @Test
    public void converterDoesntConvertBack() {
        Assertions.assertThat(mTestConverter.convertBack(true, null, null, null)).isNull();
    }

    @Test
    public void converterReturnsNullForNullFontManager() {
        IValueConverter nullTestConverter = new FontConverter(null, NullLogger.instance);

        Assertions.assertThat(nullTestConverter.convert(null, null, null, null)).isNull();
        Assertions.assertThat(nullTestConverter.convert(null, null, "default", null)).isNull();
        Assertions.assertThat(nullTestConverter.convert(null, null, "bold", null)).isNull();
    }

}