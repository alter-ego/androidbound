package solutions.alterego.androidbound.zzzztoremove;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.TimeUnit;

import solutions.alterego.androidbound.zzzztoremove.reactive.IDisposable;

public class UiThreadScheduler extends HandlerScheduler {

    public static final UiThreadScheduler instance = new UiThreadScheduler();

    private static IDisposable nullDisposable = new IDisposable() {
        @Override
        public void dispose() {
        }
    };

    public UiThreadScheduler() {
        super(new Handler(Looper.getMainLooper()));
    }

    @Override
    public IDisposable schedule(final Runnable runnable, long delay, TimeUnit unit) {
        if (delay <= 0 && handler.getLooper().getThread().getId() == Thread.currentThread().getId()) {
            runnable.run();
            return nullDisposable;
        } else {
            return super.schedule(runnable, delay, unit);
        }
    }
}
