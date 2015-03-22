package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.util.concurrent.TimeUnit;

import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;

public final class Observables {

    public final static <T> Observable<T> from(final IObservable<T> source) {
        if (source instanceof Observable<?>) {
            return (Observable<T>) source;
        }

        return asObservable(source);
    }

    public final static Observable<String> from(INotifyPropertyChanged source) {
        return from(source.onPropertyChanged());
    }

    public final static <T> Observable<T> asObservable(final IObservable<T> source) {
        return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
            public IDisposable invoke(final IObserver<T> observer) {
                return source.subscribe(observer);
            }
        });
    }

    public static Observable<Long> interval(final long initialDelay, final long period, final TimeUnit unit) {
        return interval(initialDelay, period, unit, ThreadPoolScheduler.instance);
    }

    public static Observable<Long> every(final long period, final TimeUnit unit, IScheduler scheduler) {
        return interval(0, period, unit, scheduler);
    }

    public static Observable<Long> every(final long period, final TimeUnit unit) {
        return every(period, unit, ThreadPoolScheduler.instance);
    }

    public static Observable<Long> interval(final long initialDelay, final long period, final TimeUnit unit, final IScheduler scheduler) {
        return new Observable<Long>(new Func<IObserver<Long>, IDisposable>() {
            public IDisposable invoke(final IObserver<Long> observer) {
                return new IDisposable() {
                    private boolean disposed = false;

                    private Object lock = new Object();

                    private Runnable runnable = new Runnable() {
                        long count = 0;

                        @Override
                        public void run() {
                            observer.onNext(count++);
                            synchronized (lock) {
                                if (disposed) {
                                    return;
                                }
                                currentDisposable = scheduler.schedule(runnable, period, unit);
                            }
                        }
                    };

                    private IDisposable currentDisposable = scheduler.schedule(runnable, initialDelay, unit);

                    @Override
                    public void dispose() {
                        synchronized (lock) {
                            disposed = true;
                            currentDisposable.dispose();
                        }
                    }
                };

                // Old implementation, the scheduler was always stressed by disposed schedules
                //                return scheduler.schedule(new Runnable() {
                //                    long count = 0;
                //
                //                    public void run() {
                //                        observer.onNext(count++);
                //                        scheduler.schedule(this, period, unit);
                //                    }
                //                }, initialDelay, unit);
            }
        });
    }

    public static <T, TResult> Observable<TResult> create(final T initialState, final Predicate<T> condition, final Func<T, T> iterate,
            final Func<T, TResult> selector) {
        return create(initialState, condition, iterate, selector, CurrentThreadScheduler.instance);
    }

    public static <T, TResult> Observable<TResult> create(final T initialState, final Predicate<T> condition, final Func<T, T> iterate,
            final Func<T, TResult> selector, final IScheduler scheduler) {
        return new Observable<TResult>(new Func<IObserver<TResult>, IDisposable>() {
            public IDisposable invoke(final IObserver<TResult> observer) {
                return scheduler.schedule(new Runnable() {
                    T state = initialState;

                    boolean first = true;

                    @SuppressWarnings("unchecked")
                    public void run() {
                        boolean canInvoke;
                        TResult local = null;
                        try {
                            if (first) {
                                first = false;
                            } else {
                                if (iterate != null) {
                                    state = iterate.invoke(state);
                                } else {
                                    canInvoke = false;
                                }
                            }
                            canInvoke = condition.invoke(state);
                            if (canInvoke) {
                                local = (TResult) (selector == null ? state : selector.invoke(state));
                            }
                        } catch (Exception e) {
                            observer.onError(e);
                            return;
                        }
                        if (canInvoke) {
                            observer.onNext(local);
                            run();
                        } else {
                            observer.onCompleted();
                        }
                    }
                });
            }
        });
    }

    public static Observable<Float> linear(final Float start, final Float end, final long duration, final long rate, TimeUnit unit,
            IScheduler scheduler) {
        long durationMsec = TimeUnit.MILLISECONDS.convert(duration, unit);
        long rateMsec = TimeUnit.MILLISECONDS.convert(rate, unit);

        if (rateMsec <= 0 || durationMsec <= rateMsec) {
            throw new IllegalArgumentException("duration and rate must be valid");
        }

        final long startTimestamp = System.currentTimeMillis();
        final Float scaleFactor = (end - start) / (float) durationMsec;
        final boolean ascending = end >= start;

        return Observables
                .every(rate, TimeUnit.MILLISECONDS, scheduler)
                .select(new Func<Long, Float>() {
                    @Override
                    public Float invoke(Long obj) {
                        long currentTimestamp = System.currentTimeMillis();

                        Float retval = start + (currentTimestamp - startTimestamp) * scaleFactor;
                        if ((ascending && retval > end) || (!ascending && retval < end)) {
                            retval = end;
                        }

                        return retval;
                    }
                })
                .takeUntil(new Predicate<Float>() {
                    @Override
                    public Boolean invoke(Float obj) {
                        return obj != null && !obj.equals(end);
                    }
                });
    }

    public static Observable<Long> range(final long start, final long count) {
        return range(start, count, CurrentThreadScheduler.instance);
    }

    public static Observable<Long> range(final long start, final long count, IScheduler scheduler) {
        final long end = start + count;

        return create(start, new Predicate<Long>() {
                    public Boolean invoke(Long obj) {
                        return obj.longValue() < end;
                    }
                },
                new Func<Long, Long>() {
                    public Long invoke(Long obj) {
                        return obj.longValue() + 1;
                    }
                },
                new Func<Long, Long>() {
                    public Long invoke(Long obj) {
                        return obj;
                    }
                }, scheduler);
    }

    public static <T> Observable<T> empty() {
        return Observables.create(null, Predicate.alwaysFalse, null, null);
    }

    public static <T> Observable<T> yield(T item, IScheduler scheduler) {
        return Observables.create(item, null, null, null, scheduler);
    }

    public static <T> Observable<T> yield(Func<Void, T> itemFactory, IScheduler scheduler) {
        return Observables.create(itemFactory, null, null, new Func<Func<Void, T>, T>() {
            public T invoke(Func<Void, T> obj) {
                return obj.invoke(null);
            }
        }, scheduler);
    }
}