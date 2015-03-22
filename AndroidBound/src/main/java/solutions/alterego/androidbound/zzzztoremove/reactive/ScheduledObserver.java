package solutions.alterego.androidbound.zzzztoremove.reactive;

import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;

class ScheduledObserver<T> implements IObserver<T>, IDisposable {

    private LinkedBlockingQueue<Runnable> queue;

    private IScheduler scheduler;

    private IDisposable disposable;

    private IObserver<T> observer;

    private boolean isStopped;

    private boolean hasFaulted;

    private boolean isAcquired;

    public ScheduledObserver(IScheduler scheduler, IObserver<T> observer) {
        queue = new LinkedBlockingQueue<Runnable>();
        this.scheduler = scheduler;
        this.observer = observer;
    }

    public void onNext(final T obj) {
        queue.offer(new Runnable() {
            public void run() {
                observer.onNext(obj);
            }
        });
        ensureActive();
    }

    public void onError(final Exception exc) {
        if (isStopped) {
            return;
        }

        queue.offer(new Runnable() {
            public void run() {
                observer.onError(exc);
            }
        });
        ensureActive();
    }

    public void onCompleted() {
        if (isStopped) {
            return;
        }

        queue.offer(new Runnable() {
            public void run() {
                observer.onCompleted();
            }
        });
        ensureActive();
    }

    public void ensureActive() {
        boolean canProceed = false;

        if (!hasFaulted && queue.size() > 0) {
            canProceed = !isAcquired;
            isAcquired = true;
        }

        if (canProceed) {
            // Sometimes could bring to StackOverflowException

            //        	Runnable runnable = new Runnable() {
            //        		public void run() {
            //        			Runnable inner;
            //        			if(queue.size() > 0)
            //        				inner = queue.poll();
            //        			else {
            //        				isAcquired = false;
            //        				return;
            //        			}
            //        			try {
            //        				inner.run();        				
            //        			}
            //        			catch(Exception e) {
            //        				queue.clear();
            //        				hasFaulted = true;
            //        			}
            //        			run();
            //        		}
            //        	};
            Runnable runnable = new Runnable() {
                public void run() {
                    while (queue.size() > 0) {
                        Runnable inner = queue.poll();
                        try {
                            inner.run();
                        } catch (Exception e) {
                            Log.e("ScheduledObserver",
                                    "Caught exception in scheduled item: " + e + ", position in stack: "
                                            + e.getStackTrace()[0]);
                            observer.onError(e);
                            queue.clear();
                            hasFaulted = true;
                        }
                    }

                    isAcquired = false;
                }
            };
            this.disposable = scheduler.schedule(runnable);
        }
    }

    public void dispose() {
        isStopped = true;
        if (disposable != null) {
            disposable.dispose();
        }
    }
}
