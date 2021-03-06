package solutions.alterego.androidbound.converters;

import java.util.HashMap;
import java.util.Map;

import solutions.alterego.androidbound.converters.interfaces.IValueConverter;
import solutions.alterego.androidbound.converters.interfaces.IValueConverterProvider;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public class ValueConverterService implements IValueConverterProvider, INeedsLogger {

    Map<String, IValueConverter> converters = new HashMap<String, IValueConverter>();

    private ILogger logger;

    public ValueConverterService(ILogger logger) {
        setLogger(logger);
    }

    public void registerConverter(IValueConverter converter) {
        logger.debug("Registering converter " + converter.getBindingName());
        converters.put(converter.getBindingName(), converter);
    }

    public IValueConverter findConverter(String name) {
        if (name != null && converters.containsKey(name)) {
            logger.info("ValueConverterService using non-default converter " + name);
            return converters.get(name);
        }

        return DefaultConverter.instance;
    }

    public void setLogger(ILogger logger) {
        this.logger = logger.getLogger(this);
    }

}
