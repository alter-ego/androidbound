
package com.alterego.androidbound.zzzztoremove;

import java.util.concurrent.TimeUnit;

import android.os.Handler;

import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;

public class HandlerScheduler implements IScheduler {
    protected Handler handler;

    public HandlerScheduler(Handler handler) {
        this.handler = handler;
    }

    public IDisposable schedule(Runnable runnable) {
        return schedule(runnable, 0, TimeUnit.MILLISECONDS);
    }

    public IDisposable schedule(final Runnable runnable, long delay, TimeUnit unit) {
        handler.postDelayed(runnable, unit.toMillis(delay));
        return new IDisposable() {
            public void dispose() {
                handler.removeCallbacks(runnable);
            }
        };
    }

    public IDisposable schedule(final Runnable runnable, long initialDelay, final long delay,
            final TimeUnit unit) {
        return schedule(new Runnable() {
            public void run() {
                runnable.run();
                schedule(runnable, delay, unit);
            }
        }, initialDelay, unit);
    }

    public long now() {
        return System.currentTimeMillis();
    }
}
