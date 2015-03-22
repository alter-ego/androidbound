package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Subject<T> implements ISubject<T>, IDisposable {

    private List<IObserver<T>> observers;

    private boolean completed;

    private boolean isDisposed;

    private Exception exception;

    public Subject() {
        observers = new CopyOnWriteArrayList<IObserver<T>>();
    }

    public synchronized IDisposable subscribe(final IObserver<T> observer) {
        checkDisposed();

        if (!completed) {
            observers.add(observer);
            return new Subscription(this, observer);
        }

        if (exception != null) {
            observer.onError(exception);
        } else {
            observer.onCompleted();
        }
        return new IDisposable() {
            public void dispose() {
            }
        };
    }

    private void checkDisposed() {
        if (isDisposed) {
            throw new RuntimeException("Object already disposed");
        }
    }

    private synchronized void unsubscribe(IObserver<T> observer) {
        if (observers != null) {
            observers.remove(observer);
        }
    }

    public synchronized void onNext(T value) {
        checkDisposed();
        if (completed) {
            return;
        }
        if (observers != null) {
            for (IObserver<T> obs : observers) {
                obs.onNext(value);
            }
        }
    }

    public synchronized void onError(Exception error) {
        checkDisposed();
        if (completed) {
            return;
        }
        completed = true;
        exception = error;
        if (observers != null) {
            for (IObserver<T> obs : observers) {
                obs.onError(error);
            }
        }
    }

    public synchronized void onCompleted() {
        checkDisposed();
        if (completed) {
            return;
        }
        completed = true;
        if (observers != null) {
            for (IObserver<T> obs : observers) {
                obs.onCompleted();
            }
        }
    }

    public synchronized void dispose() {
        isDisposed = true;
        observers = null;
    }

    private class Subscription implements IDisposable {

        private Subject<T> subject;

        private IObserver<T> observer;

        public Subscription(Subject<T> subject, IObserver<T> observer) {
            this.subject = subject;
            this.observer = observer;
        }

        public void dispose() {
            if (observer != null) {
                subject.unsubscribe(observer);
                subject = null;
            }
            observer = null;
        }
    }
}
