package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.concurrent.TimeUnit;


public class Notification<T> {
	public static final int NEXT = 0;
	public static final int ERROR = 1;
	public static final int COMPLETED = 2;
	long delay;
	TimeUnit unit;
	T value;
	Exception exception;
	int kind;
	
	private Notification() { }
	
	public static <T> Notification<T> onNext(long delay, TimeUnit unit, T value) {
		Notification<T> result = new Notification<T>();
		result.value = value;
		result.unit = unit;
		result.delay = delay;
		result.kind = Notification.NEXT;
		return result;
	}
	
	public static <T> Notification<T> onError(long delay, TimeUnit unit, Exception exception) {
		Notification<T> result = new Notification<T>();
		result.exception = exception;
		result.unit = unit;
		result.delay = delay;
		result.kind = Notification.ERROR;
		return result;
	}
	
	public static <T> Notification<T> onCompleted(long delay, TimeUnit unit) {
		Notification<T> result = new Notification<T>();
		result.unit = unit;
		result.delay = delay;
		result.kind = Notification.COMPLETED;
		return result;
	}
	
	public IDisposable scheduleFor(final IObserver<T> observer) {
		return scheduleFor(observer, ThreadPoolScheduler.instance);
	}
	
	public void accept(IObserver<T> observer) {
		switch(kind) {
		case NEXT:
			observer.onNext(value);
			return;
		case ERROR:
			observer.onError(exception);
			return;
		case COMPLETED:
			observer.onCompleted();
			return;
		}
	}
	
	public IDisposable scheduleFor(final IObserver<T> observer, IScheduler scheduler) {
		return scheduler.schedule(new Runnable() { 
			public void run() {
				accept(observer);
			}
		}, delay, unit);
	}
}