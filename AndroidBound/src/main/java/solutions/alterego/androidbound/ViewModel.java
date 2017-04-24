package solutions.alterego.androidbound;

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
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;

@Accessors(prefix = "m")
public class ViewModel implements INeedsLogger, INotifyPropertyChanged, IDisposable, IActivityLifecycle {

    @Getter
    protected transient ILogger mLogger = NullLogger.instance;

    private transient PublishSubject<String> propertyChanges = PublishSubject.create();

    private transient WeakReference<Activity> mParentActivity;

    private boolean mDisposed = false;

    private boolean mCreated = false;

    public boolean isCreated() {
        return mCreated;
    }

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
        mCreated = false;

        if (propertyChanges != null) {
            propertyChanges.onCompleted();
        }

        if (mParentActivity != null && mParentActivity.get() != null) {
            mParentActivity.clear();
        }

        propertyChanges = PublishSubject.create(); //we recreate the subject so that we can reuse the same VM without recreating it
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        mCreated = true;
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
