package solutions.alterego.androidbound.services;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.IValueConverter;
import solutions.alterego.androidbound.interfaces.IValueConverterProvider;
import solutions.alterego.androidbound.interfaces.IValueConverterRegistry;

public class ValueConverterService implements IValueConverterRegistry, IValueConverterProvider, INeedsLogger {

    Map<String, IValueConverter> converters = new HashMap<String, IValueConverter>();

    private IAndroidLogger logger;

    public ValueConverterService(IAndroidLogger logger) {
        setLogger(logger);
    }

    public void registerConverter(String name, IValueConverter converter) {
        logger.debug("Registering converter " + name);
        converters.put(name, converter);
    }

    public IValueConverter find(String name) {
        if (converters.containsKey(name)) {
            return converters.get(name);
        }
        logger.warning("mValueConverter " + name + " not found. Returning default.");
        return DefaultConverter.instance;
    }

    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }

}
