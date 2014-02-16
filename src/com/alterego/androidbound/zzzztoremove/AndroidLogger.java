
package com.alterego.androidbound.zzzztoremove;

import android.util.Log;

import com.alterego.androidbound.helpers.Pattern;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * AndroidLogger is the Android specific implementation of the {@link ILogger}
 * interface providing logging under a unified tag. It can be initialized
 * simply, by providing a String tag, or by also providing a logging level
 * (default level is DEBUG).
 */

public class AndroidLogger implements ILogger {
    private String mTag;

    public static enum LoggingLevel {
        VERBOSE, DEBUG, NORMAL
    }

    ;

    private int mLogLevel;
    private LoggingLevel level;
    private Map<String, LoggingLevel> presetLevels;
    private Map<Pattern, LoggingLevel> presetPatterns;

    /**
     * Inits the AndroidLogger with specified tag and default debugging level
     * which is DEBUG (Log.d).
     *
     * @param tag Logging tag
     */

    public AndroidLogger(String tag) {
        // DEFAULT LOGGING LEVEL = DEBUG
        // this.tag = tag;
        this(tag, LoggingLevel.DEBUG);
    }

    /**
     * Inits the AndroidLogger with specified tag and with specified logging
     * level (there are 3 levels on Android, see {@link Log}.
     *
     * @param tag   Logging tag
     * @param level Logging level = {VERBOSE (Log.v, only for debugging
     *              releases), DEBUG (Log.d, preferably only for debugging
     *              releases), NORMAL (Log.i, Log.w and Log.e; for published
     *              releases)}
     */

    public AndroidLogger(String tag, LoggingLevel level) {

        this.mTag = tag;
        setLoggingLevel(level);

    }

    /**
     * Sets the logging level for the logger (there are 3 levels on Android, see
     * {@link Log}.
     *
     * @param level Logging level = {VERBOSE (Log.v, only for debugging
     *              releases), DEBUG (Log.d, preferably only for debugging
     *              releases), NORMAL (Log.i, Log.w and Log.e; for published
     *              releases)}
     */
    public void setLoggingLevel(LoggingLevel level) {
        this.level = level;

        if (level == LoggingLevel.VERBOSE)
            mLogLevel = 0;
        if (level == LoggingLevel.DEBUG)
            mLogLevel = 1;
        if (level == LoggingLevel.NORMAL)
            mLogLevel = 2;

    }

    public void setLoggingLevelFor(Class<? extends Object> cls, LoggingLevel level) {
        if (this.presetLevels == null) {
            this.presetLevels = new HashMap<String, LoggingLevel>();
        }

        this.presetLevels.put(cls.getSimpleName(), level);
    }

    public void setLoggingLevelFor(String subTag, LoggingLevel level) {
        if (this.presetLevels == null) {
            this.presetLevels = new HashMap<String, LoggingLevel>();
        }

        this.presetLevels.put(subTag, level);
    }

    public void setLoggingLevelFor(Pattern regex, LoggingLevel level) {
        if (this.presetPatterns == null) {
            this.presetPatterns = new HashMap<Pattern, LoggingLevel>();
        }

        this.presetPatterns.put(regex, level);
    }

    public ILogger getLogger(Object instance) {
        return this.getLogger(instance.getClass());
    }

    public ILogger getLogger(Class<? extends Object> cls) {
        String subTag = cls.getSimpleName();

        AndroidLogger retval = new AndroidLogger(mTag + "/" + subTag, this.level);

        if (this.presetLevels != null && this.presetLevels.containsKey(subTag)) {
            retval.setLoggingLevel(this.presetLevels.get(subTag));
        } else if (this.presetPatterns != null) {
            for (Entry<Pattern, LoggingLevel> ent : this.presetPatterns.entrySet()) {
                if (ent.getKey().matcher(subTag).matches()) {
                    retval.setLoggingLevel(ent.getValue());
                    break;
                }
            }
        }

        if (this.presetLevels != null)
            retval.presetLevels = new HashMap<String, LoggingLevel>(this.presetLevels);

        if (this.presetPatterns != null)
            retval.presetPatterns = new HashMap<Pattern, LoggingLevel>(this.presetPatterns);

        return retval;
    }

    public void verbose(String content) {
        if (mLogLevel == 0)
            Log.v(mTag, content);
    }

    public void debug(String content) {
        if (mLogLevel <= 1)
            Log.d(mTag, content);
    }

    public void d(String content) {
        if (mLogLevel <= 1)
            d(mTag, content);
    }

    public void d(String tag, String content) {
        if (mLogLevel <= 1) {
            String fullClassName = Thread.currentThread().getStackTrace()[3].getClassName();
            String className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1);
            String methodName = Thread.currentThread().getStackTrace()[3].getMethodName();
            int lineNumber = Thread.currentThread().getStackTrace()[3].getLineNumber();

            Log.d(tag, className + "." + methodName + "() @ " + lineNumber + ": " + content);
        }
    }

    public void info(String content) {
        if (mLogLevel <= 2)
            Log.i(mTag, content);
    }

    public void warning(String content) {
        Log.w(mTag, content);
    }

    public void w(String content) {
        Log.w(mTag, content);
    }

    public void error(String content) {
        Log.e(mTag, content);
    }

    public void fail(String content) {
        Log.wtf(mTag, content);
    }

}
