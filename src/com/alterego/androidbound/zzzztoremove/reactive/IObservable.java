package com.alterego.androidbound.zzzztoremove.reactive;

public interface IObservable<T> {
	IDisposable subscribe(IObserver<T> observer);
}