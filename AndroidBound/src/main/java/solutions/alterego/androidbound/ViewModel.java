package solutions.alterego.androidbound;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;

public class ViewModel implements INotifyPropertyChanged, IDisposable {

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    protected boolean mDisposed = false;

    @Override
    public Observable<String> onPropertyChanged() {
        return propertyChanges;
    }

    @Override
    public void dispose() {
        if (mDisposed) {
            return;
        }

        mDisposed = true;

        if (propertyChanges != null) {
            propertyChanges.onComplete();
        }

        propertyChanges = PublishSubject.create(); //we recreate the subject so that we can reuse the same VM without recreating it
    }

    protected void raisePropertyChanged(String property) {
        propertyChanges.onNext(property);
    }

    public boolean isDisposed() {
        return mDisposed;
    }
}
