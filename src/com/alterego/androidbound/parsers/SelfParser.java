package com.alterego.androidbound.parsers;

import com.alterego.androidbound.interfaces.IParser;
import com.alterego.androidbound.zzzztoremove.ILogger;

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
