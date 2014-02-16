package com.alterego.androidbound.interfaces;


public interface IParser<T> extends INeedsLogger {
	T parse(String content);
}
