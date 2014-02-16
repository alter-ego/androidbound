
package com.alterego.androidbound.zzzztoremove;

import java.util.HashMap;
import java.util.Map;


public class CachedProvider<T> implements IContentProvider<T> {
    private Map<String, Exceptional<T>> cache = new HashMap<String, Exceptional<T>>();
    private IContentProvider<T> source;

    public CachedProvider(IContentProvider<T> source) {
        this.source = source;
    }

    public Exceptional<T> getContent(String location) {
        if (!cache.containsKey(location))
            cache.put(location, source.getContent(location));

        return cache.get(location);
    }

    public void setLogger(ILogger logger) {
        source.setLogger(logger);
    }
}
