package solutions.alterego.androidbound.factories;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import solutions.alterego.androidbound.interfaces.IDisposable;

public final class UiThreadScheduler {

    private static final UiThreadScheduler INSTANCE = new UiThreadScheduler(new Handler(Looper.getMainLooper()));

    private final Handler mHandler;

    public static UiThreadScheduler instance() {
        return INSTANCE;
    }

    private UiThreadScheduler(Handler handler) {
        mHandler = handler;
    }

    public IDisposable schedule(Runnable runnable) {
        return schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public IDisposable schedule(final Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0 && mHandler.getLooper().getThread().getId() == Thread.currentThread().getId()) {
            runnable.run();
            return nullDisposable;
        } else {
            mHandler.postDelayed(runnable, unit.toMillis(delay));
            return () -> mHandler.removeCallbacks(runnable);
        }
    }

    public IDisposable schedule(final Runnable runnable, long initialDelay, final long delay,
            final TimeUnit unit) {
        return schedule(() -> {
            runnable.run();
            schedule(runnable, delay, unit);
        }, initialDelay, unit);
    }

    private static IDisposable nullDisposable = () -> {
    };

    public long now() {
        return System.currentTimeMillis();
    }


}
