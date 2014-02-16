package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.concurrent.TimeUnit;

public interface IScheduler {
	IDisposable schedule(Runnable runnable);
	IDisposable schedule(Runnable runnable, long delay, TimeUnit unit);
	IDisposable schedule(Runnable runnable, long initialDelay, long delay, TimeUnit unit);
	long now();
}
