package solutions.alterego.androidbound.converters;

import org.fest.assertions.api.Assertions;
import org.junit.Test;

import java.util.Locale;

import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public class ValueConverterServiceTest {

    private final ValueConverterService mValueConverterService;

    private IValueConverter testConverter = new IValueConverter() {
        @Override
        public String getBindingName() {
            return "test";
        }

        @Override
        public Object convert(Object value, Class<?> targetType, Object param, Locale locale) {
            return "converted";
        }

        @Override
        public Object convertBack(Object value, Class<?> targetType, Object param, Locale locale) {
            return "convertedBack";
        }
    };

    public ValueConverterServiceTest() {
        mValueConverterService = new ValueConverterService(NullLogger.instance);
    }

    @Test
    public void registerConverter() {
        Assertions.assertThat(mValueConverterService.findConverter("test")).isInstanceOf(DefaultConverter.class);

        mValueConverterService.registerConverter(testConverter);

        Assertions.assertThat(mValueConverterService.findConverter("test")).isNotInstanceOf(DefaultConverter.class);
        Assertions.assertThat(mValueConverterService.findConverter("test").getBindingName()).isEqualTo("test");
    }

    @Test
    public void converts() {
        mValueConverterService.registerConverter(testConverter);

        Assertions.assertThat(mValueConverterService.findConverter("test").convert(null, null, null, null)).isEqualTo("converted");
    }

    @Test
    public void convertsBack() {
        mValueConverterService.registerConverter(testConverter);

        Assertions.assertThat(mValueConverterService.findConverter("test").convertBack(null, null, null, null)).isEqualTo("convertedBack");
    }

}