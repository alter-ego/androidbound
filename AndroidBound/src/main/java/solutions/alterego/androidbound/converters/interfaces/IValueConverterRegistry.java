package solutions.alterego.androidbound.converters.interfaces;

import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public interface IValueConverterRegistry {

    void registerConverter(String name, IValueConverter converter);
}
