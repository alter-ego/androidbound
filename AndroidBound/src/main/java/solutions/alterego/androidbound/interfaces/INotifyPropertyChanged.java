package solutions.alterego.androidbound.interfaces;

import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.IObservable;

public interface INotifyPropertyChanged extends IDisposable {
	IObservable<String> onPropertyChanged();
}
