package solutions.alterego.androidbound;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;

import android.app.Activity;
import android.os.Bundle;

import java.lang.ref.WeakReference;

import lombok.Getter;
import lombok.experimental.Accessors;
import rx.Observable;
import rx.subjects.PublishSubject;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

@Accessors(prefix = "m")
public abstract class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable {

    @Getter
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

    @Override
    public Observable<String> onPropertyChanged() {
        return propertyChanges;
    }

    @Override
    public void dispose() {
        if (propertyChanges != null) {
            propertyChanges.onCompleted();
            propertyChanges = null;
        }

        if (mParentActivity != null && mParentActivity.get() != null) {
            mParentActivity.clear();
        }
    }

    @Override
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

    public abstract void onCreate(Bundle outState);

    public abstract void onResume();

    public abstract void onPause();

    public abstract void onSaveInstanceState(Bundle outState);

}
