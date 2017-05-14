package solutions.alterego.androidbound.example.util;

import com.alterego.advancedandroidlogger.implementations.DetailedAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import solutions.alterego.androidbound.interfaces.ILogger;

public class AdvancedAndroidLoggerAdapter implements ILogger {

    private final DetailedAndroidLogger mLogger;

    public AdvancedAndroidLoggerAdapter(String tag, IAndroidLogger.LoggingLevel level) {
        mLogger = new DetailedAndroidLogger(tag, level);
    }

    @Override
    public void verbose(String msg) {
        mLogger.verbose(msg);
    }

    @Override
    public void debug(String msg) {
        mLogger.debug(msg);
    }

    @Override
    public void info(String msg) {
        mLogger.info(msg);
    }

    @Override
    public void warning(String msg) {
        mLogger.warning(msg);
    }

    @Override
    public void error(String msg) {
        mLogger.error(msg);
    }

    @Override
    public ILogger getLogger(Object object) {
        return this;
    }
}
