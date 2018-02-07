package solutions.alterego.androidbound;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {

    protected transient ILogger mLogger = NullLogger.instance;

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    protected boolean mDisposed = false;

    @Override
    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }

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
        try {
            propertyChanges.onNext(property);
        } catch (Exception e) {
            mLogger.error("exception when raising property changes = " + e.getMessage());
        }
    }

    public ILogger getLogger() {
        return mLogger;
    }

    public boolean isDisposed() {
        return mDisposed;
    }
}
