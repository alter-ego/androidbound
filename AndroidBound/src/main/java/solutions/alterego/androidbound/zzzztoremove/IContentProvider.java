package solutions.alterego.androidbound.zzzztoremove;

import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IContentProvider<T> extends INeedsLogger {

    Exceptional<T> getContent(String location);
}
