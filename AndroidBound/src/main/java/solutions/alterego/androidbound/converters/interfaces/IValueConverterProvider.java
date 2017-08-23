package solutions.alterego.androidbound.converters.interfaces;

public interface IValueConverterProvider {

    void registerConverter(IValueConverter converter);

    IValueConverter findConverter(String name);
}
