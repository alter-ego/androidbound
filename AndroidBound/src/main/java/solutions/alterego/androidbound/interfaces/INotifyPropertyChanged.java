package solutions.alterego.androidbound.interfaces;

import rx.Observable;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;

public interface INotifyPropertyChanged extends IDisposable {

    Observable<String> onPropertyChanged();
}
