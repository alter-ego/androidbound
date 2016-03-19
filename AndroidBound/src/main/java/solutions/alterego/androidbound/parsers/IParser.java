package solutions.alterego.androidbound.parsers;


import solutions.alterego.androidbound.interfaces.INeedsLogger;

public interface IParser<T> extends INeedsLogger {

    T parse(String content);
}
