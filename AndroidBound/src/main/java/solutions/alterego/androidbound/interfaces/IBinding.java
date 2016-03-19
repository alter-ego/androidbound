package solutions.alterego.androidbound.interfaces;

import rx.Observable;

public interface IBinding extends IDisposable, INeedsLogger {

    static final Object noValue = new Object();

    public abstract Class<?> getType();

    public abstract Object getValue();

    public abstract void setValue(Object value);

    public abstract boolean hasChanges();

    public abstract Observable<Object> getChanges();
}
