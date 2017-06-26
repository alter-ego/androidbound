package solutions.alterego.androidbound.binding.types;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import solutions.alterego.androidbound.NullLogger;
import solutions.alterego.androidbound.binding.interfaces.IBinding;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.utils.Exceptional;

public abstract class BindingBase implements IBinding, INeedsLogger {

    private static final Observable<Exceptional<Object>> NO_CHANGES = Observable.empty();

    private PublishSubject<Exceptional<Object>> mChanges = PublishSubject.create();

    private Object mSubject;

    private ILogger mLogger = NullLogger.instance;

    public BindingBase(Object subject, ILogger logger) {
        mSubject = subject;
        setLogger(logger);
    }

    public abstract Class<?> getType();

    public abstract Object getValue();

    public abstract void setValue(Object value);

    public Observable<Exceptional<Object>> getChanges() {
        if (mChanges == null) {
            return NO_CHANGES;
        }
        return mChanges.hide();
    }

    public boolean hasChanges() {
        return mChanges != null;
    }

    protected void notifyChange(Object value) {
        if (mChanges == null) {
            return;
        }
        mChanges.onNext(Exceptional.right(value));
    }

    protected void setupChanges(boolean hasChanges) {
        if (hasChanges) {
            if (mChanges == null) {
                mChanges = PublishSubject.create();
            }
        } else {
            if (mChanges != null) {
                mChanges.onComplete();
                mChanges = null;
            }
        }
    }

    protected Object getSubject() {
        return mSubject;
    }

    protected ILogger getLogger() {
        return mLogger;
    }

    public void setLogger(ILogger logger) {
        mLogger = logger.getLogger(this);
    }

    public void dispose() {
        if (mChanges != null) {
            mChanges.onComplete();
        }
        mChanges = null;
    }
}
