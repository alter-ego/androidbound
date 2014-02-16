
package com.alterego.androidbound.services;

import java.util.HashMap;
import java.util.Map;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.interfaces.IValueConverter;
import com.alterego.androidbound.interfaces.IValueConverterProvider;
import com.alterego.androidbound.interfaces.IValueConverterRegistry;

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
        //logger.info("Requested converter " + name);
        if (converters.containsKey(name))
            return converters.get(name);
        logger.warning("Converter " + name + " not found. Returning default.");
        return DefaultConverter.instance;
    }

    public void setLogger(IAndroidLogger logger) {
        this.logger = logger.getLogger(this);
    }

}
