package com.alterego.androidbound.android.cache;

import java.util.HashMap;
import java.util.Map;

import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.zzzztoremove.Exceptional;
import com.alterego.androidbound.zzzztoremove.IContentProvider;


public class CacheSystem<T> implements IContentProvider<T> {
    private Map<String, Exceptional<T>> cacheMemory = new HashMap<String, Exceptional<T>>();
    private IContentProvider<T> source;
    private ICache cache;

    public CacheSystem(IContentProvider<T> source, ICache cache) {
        this.source = source;
        this.cache = cache;
    }

    @SuppressWarnings("unchecked")
    public Exceptional<T> getContent(String location) {
        if (cache != null) {
            Object obj = cache.retrieve(location);
            if (obj == null) {
                Exceptional<T> result = (Exceptional<T>) source.getContent(location);
                if (result.hasValue())
                    cache.store(location, result.value());
                return result;
            }
            return (Exceptional<T>) (Exceptional.right(obj));
        } else {
            Exceptional<T> result = (Exceptional<T>) source.getContent(location);
            return result;
        }
    }

    public void setLogger(IAndroidLogger logger) {
        source.setLogger(logger);
    }
}
