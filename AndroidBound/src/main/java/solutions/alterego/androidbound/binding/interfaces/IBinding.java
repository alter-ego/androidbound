package solutions.alterego.androidbound.binding.interfaces;

import io.reactivex.Observable;
import solutions.alterego.androidbound.interfaces.IDisposable;
import solutions.alterego.androidbound.interfaces.INeedsLogger;
import solutions.alterego.androidbound.utils.Exceptional;

public interface IBinding extends IDisposable, INeedsLogger {

    Object noValue = new Object();

    Class<?> getType();

    Object getValue();

    void setValue(Object value);

    void addValue(Object object);

    boolean hasChanges();

    Observable<Exceptional<Object>> getChanges();

    void removeValue(Object result);
}
