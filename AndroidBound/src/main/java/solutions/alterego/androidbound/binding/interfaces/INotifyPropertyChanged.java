package solutions.alterego.androidbound.binding.interfaces;

import io.reactivex.Observable;
import solutions.alterego.androidbound.interfaces.IDisposable;

public interface INotifyPropertyChanged extends IDisposable {

    Observable<String> onPropertyChanged();
}
