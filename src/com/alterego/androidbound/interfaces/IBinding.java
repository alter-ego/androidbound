package com.alterego.androidbound.interfaces;

import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;

public interface IBinding extends IDisposable, INeedsLogger {
	static final Object noValue = new Object();
	
	public abstract Class<?> getType();
	public abstract Object getValue();
	public abstract void setValue(Object value);
	public abstract boolean hasChanges();
	public abstract IObservable<Object> getChanges();
}
