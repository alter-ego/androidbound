package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;

public class ReplaySubject<T> implements ISubject<T>, IDisposable {
	private List<ScheduledObserver<T>> observers;
	private boolean completed;
	private boolean isDisposed;
	private List<Notification<T>> values;
	private int bufferSize;
	private IScheduler scheduler;

	public ReplaySubject() {
		this(Integer.MAX_VALUE, CurrentThreadScheduler.instance);
	}
	
	public ReplaySubject(int bufferSize) {
		this(bufferSize, CurrentThreadScheduler.instance);
	}
	
	public ReplaySubject(int bufferSize, IScheduler scheduler) {
		this.bufferSize = bufferSize;
		this.scheduler = scheduler;
		observers = new CopyOnWriteArrayList<ScheduledObserver<T>>();
		values = new ArrayList<Notification<T>>();
	}

    private void enqueue(Notification<T> n)
    {
        values.add(n);
        if(n.kind == Notification.NEXT && values.size() > bufferSize)
			values.remove(0);
    }

	private void checkDisposed() {
		if(isDisposed)
			throw new RuntimeException("Object already disposed");
	}

	private synchronized void unsubscribe(IObserver<T> observer) {
		if(observers != null)
			observers.remove(observer);
	}

	public IDisposable subscribe(final IObserver<T> observer) {
        ScheduledObserver<T> observer2 = new ScheduledObserver<T>(scheduler, observer);
        RemovableDisposable disposable = new RemovableDisposable(this, observer2);
        checkDisposed();
        observers.add(observer2);
        for(Notification<T> notification : values)
        	notification.accept(observer2);
        observer2.ensureActive();
        return disposable;
	}

	public synchronized void onNext(T value) {
        checkDisposed();
        if(!completed) {
	        enqueue(Notification.<T>onNext(0, TimeUnit.MILLISECONDS, value));
	        for(ScheduledObserver<T> observer : observers)
	            observer.onNext(value);
        }
	    if(observers != null)
	        for(ScheduledObserver<T> observer : observers)
	            observer.ensureActive();
	}

	public synchronized void onError(Exception error) {
		checkDisposed();
	    if (!completed) {
	        completed = true;
	        enqueue(Notification.<T>onError(0, TimeUnit.MILLISECONDS, error));
	        for(ScheduledObserver<T> observer : observers)
	            observer.onError(error);
	    }
	    if(observers != null)
	        for(ScheduledObserver<T> observer : observers)
	            observer.ensureActive();
	}

	public synchronized void onCompleted() {
    	checkDisposed();
        if(!completed)
        {
            completed = true;
            enqueue(Notification.<T>onCompleted(0, TimeUnit.MILLISECONDS));
            for(ScheduledObserver<T> observer : observers)
                observer.onCompleted();
        }
        if(observers != null)
        	for(ScheduledObserver<T> observer : observers)
        		observer.ensureActive();
	}

	public synchronized void dispose() {
		isDisposed = true;
		observers = null;
	}
	
    private class RemovableDisposable implements IDisposable {
        private ScheduledObserver<T> observer;
        private ReplaySubject<T> subject;

        public RemovableDisposable(ReplaySubject<T> subject, ScheduledObserver<T> observer) {
            this.subject = subject;
            this.observer = observer;
        }

        public void dispose() {
            observer.dispose();
            subject.unsubscribe(observer);
        }
    }
}
