package solutions.alterego.androidbound;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;

public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {

    protected transient IAndroidLogger mLogger = NullAndroidLogger.instance;

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    protected void raisePropertyChanged(String property) {
        try {
            propertyChanges.onNext(property);
        } catch (Exception e) {
            mLogger.error("exception when raising property changes = " + e.getMessage());
        }
    }

    public Observable<String> onPropertyChanged() {
        return propertyChanges;
    }

    public void dispose() {
        propertyChanges.onCompleted();
        propertyChanges = null;
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }
}
