
package com.alterego.androidbound.zzzztoremove;

public interface ILogger {
    ILogger getLogger(Object instance);

    void debug(String content);

    void warning(String content);

    void error(String content);

    void fail(String content);

    void verbose(String content);

    void info(String content);

    void d(String tag, String content);

    void d(String content);

}
