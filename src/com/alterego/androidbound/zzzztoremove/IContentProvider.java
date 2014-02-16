package com.alterego.androidbound.zzzztoremove;

import com.alterego.androidbound.interfaces.INeedsLogger;

public interface IContentProvider<T> extends INeedsLogger {
	Exceptional<T> getContent(String location);
}
