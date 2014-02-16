package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class ThreadPoolScheduler implements IScheduler {
	// Parallel implementation of scheduler
	// not so efficient, overhead is added because it is using two thread pool, one for timing only purposes
	// the other is the real worker thread pool
	// could be improved!!
	public static final ThreadPoolScheduler instance = new ThreadPoolScheduler();
	
    protected final ExecutorService pool;
    protected final ScheduledExecutorService timingPool;
    
    public ThreadPoolScheduler() {
    	timingPool = Executors.newSingleThreadScheduledExecutor();
        pool = Executors.newCachedThreadPool(); //Executors.newScheduledThreadPool(1);
    }
    
    public IDisposable schedule(final Runnable runnable) {
        final Future<?> f = pool.submit(new Runnable() {
        	@Override
        	public void run() {
        		runnable.run();
        	}
        });
        return new IDisposable() {
            public void dispose() {
                f.cancel(true);
            }
        };
    }

    public IDisposable schedule(final Runnable runnable, final long delay, final TimeUnit unit) {
    	final ScheduledFuture<?> f = timingPool.schedule(new Runnable() {
    		@Override
    		public void run() {
    			pool.submit(new Runnable() {
    				@Override
    				public void run() {
    					runnable.run();
    				}
    			});
    		}
    	}, delay, unit);
        return new IDisposable() {
            public void dispose() {
                f.cancel(true);
            }
        };
    }

    public IDisposable schedule(final Runnable runnable, final long initialDelay, final long delay, final TimeUnit unit) {
    	final ScheduledFuture<?> f = timingPool.scheduleWithFixedDelay(new Runnable() {
    		@Override
    		public void run() {
    			pool.submit(new Runnable() {
    				@Override
    				public void run() {
    					runnable.run();
    				}
    			});
    		}
    	}, initialDelay, delay, unit);
    	return new IDisposable() { 
    		public void dispose() {
    			f.cancel(true);
    		}
    	};
    }

	public long now() {
		return System.currentTimeMillis();
	}
}