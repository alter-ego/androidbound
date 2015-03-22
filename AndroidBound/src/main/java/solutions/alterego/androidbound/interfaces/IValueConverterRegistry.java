package solutions.alterego.androidbound.interfaces;

public interface IValueConverterRegistry {
	void registerConverter(String name, IValueConverter converter);
}
