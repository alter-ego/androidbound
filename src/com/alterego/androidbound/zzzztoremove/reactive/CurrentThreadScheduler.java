package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

public class CurrentThreadScheduler implements IScheduler {
	public final static CurrentThreadScheduler instance = new CurrentThreadScheduler();
	
	private static ThreadLocal<Trampoline> trampoline = new ThreadLocal<Trampoline>() {
		protected Trampoline initialValue() {
			return new Trampoline();
		}
	};

	public IDisposable schedule(Runnable runnable) {
		return schedule(runnable, 0, TimeUnit.MILLISECONDS);
	}
	
	public long now() {
		return System.currentTimeMillis();
	}

	public IDisposable schedule(Runnable runnable, long delay, TimeUnit unit) {
		ScheduledItem action = new ScheduledItem(runnable, now() + unit.toMillis(delay));
		trampoline.get().submit(action);
		return action.disposable;
	}
	
	public boolean isScheduleRequired() {
		return trampoline.get().isRunning();
	}

	public IDisposable schedule(final Runnable runnable, long initialDelay, final long delay, final TimeUnit unit) {
		return schedule(new Runnable() { 
			public void run() { 
				runnable.run();
				schedule(runnable, delay, unit);
			}
		}, initialDelay, unit);
	}

    private static class Trampoline implements IDisposable {
    	private boolean running;
    	private Queue<ScheduledItem> actions;
    	
        public Trampoline() {
        	this.actions = new PriorityQueue<ScheduledItem>(16, new Comparator<ScheduledItem>() {
				@Override
				public int compare(ScheduledItem lhs, ScheduledItem rhs) {
					return (int)(lhs.dueTime - rhs.dueTime);
				}
        	});
        }

        public void dispose() {
        	for(ScheduledItem item: this.actions) {
        		item.disposable.dispose();
        	}
        	
        	this.actions.clear();
            this.actions = null;
        }
        
        public void submit(ScheduledItem item) {
        	this.actions.offer(item);
        	this.run();
        }
        
        public boolean isRunning() {
        	return this.running;
        }

        private void run() {
        	if(this.running) {
        		return;
        	}
        	
        	this.running = true;
        	
            while (!this.actions.isEmpty()) {
                ScheduledItem item = this.actions.poll();
                if(item.isCancelled) {
                	continue;
                }
                
                long timeLeft = item.dueTime - System.currentTimeMillis();
            	if(timeLeft > 0) {
					try {
						Thread.sleep(timeLeft);
					} catch (InterruptedException e) {
						break;
					}
            	}
            	
                if(item.isCancelled) {
                	continue;
                }
            	
                item.invoke();
            }
            
            this.running = false;
        }
    }
}
