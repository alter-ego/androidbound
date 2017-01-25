package solutions.alterego.androidbound.parsers;

import solutions.alterego.androidbound.interfaces.ILogger;

public class SelfParser implements IParser<String> {

    public final static IParser<String> instance = new SelfParser();

    @Override
    public void setLogger(ILogger logger) {
    }

    @Override
    public String parse(String content) {
        return content;
    }
}
