package solutions.alterego.androidbound.converters.interfaces;

import solutions.alterego.androidbound.converters.interfaces.IValueConverter;

public interface IValueConverterProvider {

    IValueConverter find(String name);
}
