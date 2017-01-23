package solutions.alterego.androidbound.interfaces;

public interface ILogger {

    /**
     * Send a verbose log message.
     *
     * @param msg The message you would like logged.
     */
    void verbose(String msg);

    /**
     * Send a debug log message.
     *
     * @param msg The message you would like logged.
     */
    void debug(String msg);

    /**
     * Send an info log message.
     *
     * @param msg The message you would like logged.
     */
    void info(String msg);

    /**
     * Send a warn log message.
     *
     * @param msg The message you would like logged.
     */
    void warning(String msg);

    /**
     * Send an error log message.
     *
     * @param msg The message you would like logged.
     */
    void error(String msg);

    /**
     * Returns an instance of ILogger for a specific object.
     *
     * @param object Object for which you need a logger
     * @return ILogger  ILogger instance
     */
    ILogger getLogger(Object object);

}
