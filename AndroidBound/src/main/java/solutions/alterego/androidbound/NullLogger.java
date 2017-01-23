package solutions.alterego.androidbound;

import solutions.alterego.androidbound.interfaces.ILogger;

public class NullLogger implements ILogger {

    public static final ILogger instance = new NullLogger();

    @Override
    public void verbose(String msg) {

    }

    @Override
    public void debug(String msg) {

    }

    @Override
    public void info(String msg) {

    }

    @Override
    public void warning(String msg) {

    }

    @Override
    public void error(String msg) {

    }

    @Override
    public ILogger getLogger(Object object) {
        return instance;
    }
}
