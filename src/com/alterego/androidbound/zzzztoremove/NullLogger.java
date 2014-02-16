
package com.alterego.androidbound.zzzztoremove;


public class NullLogger implements ILogger {
    public static final ILogger instance = new NullLogger();

    private NullLogger() {
    }

    public ILogger getLogger(Object instance) {
        return this;
    }

    public void debug(String content) {
    }

    public void warning(String content) {
    }

    public void error(String content) {
    }

    public void fail(String content) {
    }

    public void verbose(String content) {
    }

    public void info(String content) {

    }

    @Override
    public void d(String tag, String content) {

    }

    @Override
    public void d(String content) {

    }
}
