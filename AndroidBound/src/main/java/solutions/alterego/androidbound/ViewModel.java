package solutions.alterego.androidbound;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import solutions.alterego.androidbound.zzzztoremove.reactive.IObservable;
import solutions.alterego.androidbound.zzzztoremove.reactive.ISubject;
import solutions.alterego.androidbound.zzzztoremove.reactive.Subject;

public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {

    protected transient IAndroidLogger mLogger = NullAndroidLogger.instance;

    private transient ISubject<String> propertyChanges = new Subject<String>();

    protected void raisePropertyChanged(String property) {
        try {
            propertyChanges.onNext(property);
        } catch (Exception e) {
            mLogger.error("exception when raising property changes = " + e.getMessage());
        }
    }

    public IObservable<String> onPropertyChanged() {
        return propertyChanges;
    }

    public void dispose() {
        propertyChanges.dispose();
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }
}
