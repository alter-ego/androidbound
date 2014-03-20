package com.alterego.androidbound.binds;

import com.alterego.advancedandroidlogger.implementations.NullAndroidLogger;
import com.alterego.advancedandroidlogger.interfaces.IAndroidLogger;
import com.alterego.androidbound.interfaces.IBinding;
import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.ISubject;
import com.alterego.androidbound.zzzztoremove.reactive.Observables;
import com.alterego.androidbound.zzzztoremove.reactive.Subject;

public abstract class BindingBase implements IBinding, INeedsLogger {

    private static final IObservable<Object> NO_CHANGES = Observables.<Object>empty();

    private ISubject<Object> mChanges;

    private Object mSubject;

    private IAndroidLogger mLogger = NullAndroidLogger.instance;

    public BindingBase(Object subject, IAndroidLogger logger) {
        mSubject = subject;
        setLogger(logger);
    }

    public abstract Class<?> getType();

    public abstract Object getValue();

    public abstract void setValue(Object value);

    public IObservable<Object> getChanges() {
        if (mChanges == null) {
            return NO_CHANGES;
        }
        return mChanges;
    }

    public boolean hasChanges() {
        return mChanges != null;
    }

    protected void notifyChange(Object value) {
        if (mChanges == null) {
            return;
        }
        mChanges.onNext(value);
    }

    protected void setupChanges(boolean hasChanges) {
        if (hasChanges) {
            if (mChanges == null) {
                mChanges = new Subject<Object>();
            }
        } else {
            if (mChanges != null) {
                mChanges.dispose();
                mChanges = null;
            }
        }
    }

    protected Object getSubject() {
        return mSubject;
    }

    protected IAndroidLogger getLogger() {
        return mLogger;
    }

    public void setLogger(IAndroidLogger logger) {
        mLogger = logger.getLogger(this);
    }

    public void dispose() {
        if (mChanges != null) {
            mChanges.dispose();
        }
        mChanges = null;
    }
}
