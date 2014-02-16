package com.alterego.androidbound.interfaces;

import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;

public interface INotifyPropertyChanged extends IDisposable {
	IObservable<String> onPropertyChanged();
}
