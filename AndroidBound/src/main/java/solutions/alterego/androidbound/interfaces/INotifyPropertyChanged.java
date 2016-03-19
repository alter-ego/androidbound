package solutions.alterego.androidbound.interfaces;

import rx.Observable;

public interface INotifyPropertyChanged extends IDisposable {

    Observable<String> onPropertyChanged();
}
