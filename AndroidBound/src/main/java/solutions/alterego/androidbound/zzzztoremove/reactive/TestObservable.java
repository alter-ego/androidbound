package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;


public class TestObservable<T> implements IObservable<T> {

    private List<Notification<T>> notifications = new ArrayList<Notification<T>>();

    private long lastTimeout;

    private boolean completed;

    private IScheduler scheduler;

    public TestObservable() {
        this(CurrentThreadScheduler.instance);
    }

    public TestObservable(IScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public TestObservable<T> addOnNext(long delayMillis, T value) {
        if (!completed) {
            notifications.add(Notification.onNext(lastTimeout + delayMillis, TimeUnit.MILLISECONDS, value));
            lastTimeout += delayMillis;
        }
        return this;
    }

    public void addOnError(long delayMillis, Exception exception) {
        notifications.add(Notification.<T>onError(lastTimeout + delayMillis, TimeUnit.MILLISECONDS, exception));
        completed = true;
    }

    public void addOnCompleted(long delayMillis) {
        notifications.add(Notification.<T>onCompleted(lastTimeout + delayMillis, TimeUnit.MILLISECONDS));
        completed = true;
    }

    public IDisposable subscribe(IObserver<T> observer) {
        final List<IDisposable> disposables = new ArrayList<IDisposable>();
        for (Notification<T> notification : notifications) {
            disposables.add(notification.scheduleFor(observer, scheduler));
        }
        return new IDisposable() {
            public void dispose() {
                for (IDisposable disposable : disposables) {
                    disposable.dispose();
                }
            }
        };
    }
}
