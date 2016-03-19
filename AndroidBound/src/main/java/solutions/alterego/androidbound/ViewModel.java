package solutions.alterego.androidbound;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;

import java.lang.ref.WeakReference;

import lombok.experimental.Accessors;
import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

@Accessors(prefix = "m")
public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {

    protected transient IAndroidLogger mLogger = NullAndroidLogger.instance;

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    protected transient WeakReference<Activity> mParentActivity;

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

    public void setParentActivity(Activity activity) {
        mParentActivity = new WeakReference<Activity>(activity);
    }

    public Activity getParentActivity() {
        if (mParentActivity != null && mParentActivity.get() != null) {
            return mParentActivity.get();
        }

        return null;
    }


}
