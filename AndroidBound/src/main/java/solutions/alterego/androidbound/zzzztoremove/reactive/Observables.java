package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.functions.Func1;
import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;

public final class Observables {

    public final static <T> Observable<T> from(final Observable<T> source) {
        if (source instanceof Observable<?>) {
            return source;
        }
        return asObservable(source);
    }

    public final static Observable<String> from(INotifyPropertyChanged source) {
        return from(source.onPropertyChanged());
    }

    public final static <T> Observable<T> asObservable(final Observable<T> source) {
        return Observable.create(new Observable.OnSubscribe<T>() {
            @Override
            public void call(Subscriber<? super T> subscriber) {
                source.subscribe(subscriber);
            }
        });
    }

    public static Observable<Float> linear(final Float start, final Float end, final long duration, final long rate, TimeUnit unit,
            Scheduler scheduler) {
        long durationMsec = TimeUnit.MILLISECONDS.convert(duration, unit);
        long rateMsec = TimeUnit.MILLISECONDS.convert(rate, unit);

        if (rateMsec <= 0 || durationMsec <= rateMsec) {
            throw new IllegalArgumentException("duration and rate must be valid");
        }

        final long startTimestamp = System.currentTimeMillis();
        final Float scaleFactor = (end - start) / (float) durationMsec;
        final boolean ascending = end >= start;

        return Observable
                .interval(rate, TimeUnit.MILLISECONDS, scheduler)
                .map(new Func1<Long, Float>() {
                    @Override
                    public Float call(Long aLong) {
                        long currentTimestamp = System.currentTimeMillis();

                        Float retval = start + (currentTimestamp - startTimestamp) * scaleFactor;
                        if ((ascending && retval > end) || (!ascending && retval < end)) {
                            retval = end;
                        }

                        return retval;
                    }
                })
                .takeUntil(Observable.just(end));
    }

    public static <T> Observable<T> empty() {
        return Observable.empty();
    }
}