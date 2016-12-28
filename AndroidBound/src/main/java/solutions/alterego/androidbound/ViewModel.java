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
import solutions.alterego.androidbound.android.interfaces.IActivityLifecycle;
import solutions.alterego.androidbound.binding.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

@Accessors(prefix = "m")
public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable, IActivityLifecycle {

    @Getter
    protected transient IAndroidLogger mLogger = NullAndroidLogger.instance;

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    private transient WeakReference<Activity> mParentActivity;

    private boolean mDisposed = false;

    protected void raisePropertyChanged(String property) {
        try {
            propertyChanges.onNext(property);
        } catch (Exception e) {
            mLogger.error("exception when raising property changes = " + e.getMessage());
        }
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

    @Override
    public void setLogger(IAndroidLogger logger) {
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
            propertyChanges.onCompleted();
            propertyChanges = null;
        }

        if (mParentActivity != null && mParentActivity.get() != null) {
            mParentActivity.clear();
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        //do nothing, to be overridden
    }

    @Override
    public void onStart() {
        //do nothing, to be overridden
    }

    @Override
    public void onRestart() {
        //do nothing, to be overridden
    }

    @Override
    public void onResume() {
        //do nothing, to be overridden
    }

    @Override
    public void onPause() {
        //do nothing, to be overridden
    }

    @Override
    public void onStop() {
        //do nothing, to be overridden
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //do nothing, to be overridden
    }

    @Override
    public void onDestroy() {
        if (!mDisposed) {
            dispose();
        }
    }

}
