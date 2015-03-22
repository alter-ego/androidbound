package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.util.PriorityQueue;
import java.util.concurrent.TimeUnit;

public class TestScheduler implements IScheduler {
	private PriorityQueue<ScheduledItem> queue;
	private long now;
	private boolean isEnabled;
	
	public TestScheduler() {
		queue = new PriorityQueue<ScheduledItem>(400);	
		now = 0;
	}
	
	public void advanceBy(long delay, TimeUnit unit) {
		long dueTime = now() + unit.toMillis(delay);
		if(now() >= dueTime)
			return;
		advanceTo(dueTime, TimeUnit.MILLISECONDS);
	}
	
	public void advanceTo(long dueTime, TimeUnit unit) {
		long time = unit.toMillis(dueTime);
		if(now() >= time)
			return;
		if(!isEnabled) {
			isEnabled = true;
			do {
				ScheduledItem next = getNext();
				if(next != null && next.dueTime <= time) {
					if(next.dueTime > now())
						now = next.dueTime;
					next.invoke();
				}
				else 
					isEnabled = false;
			}
			while(isEnabled);
			now = time;
		}
	}
	
	public void start() {
		if(!isEnabled) {
			isEnabled = true;
			do {
				ScheduledItem next = getNext();
				if(next != null) {
					if(next.dueTime > now())
						now = next.dueTime;
					next.invoke();
				}
				else
					isEnabled = false;
			}
			while(isEnabled);
		}
	}
	
	public void stop() {
		isEnabled = false;
	}
	
	public IDisposable schedule(Runnable runnable) {
		return schedule(runnable, 0, TimeUnit.MILLISECONDS);
	}

	public IDisposable schedule(final Runnable runnable, long delay, TimeUnit unit) {
		long dueTime = now() + unit.toMillis(delay);
		final ScheduledItem item = new ScheduledItem(dueTime);
		item.setRunnable(new Runnable() {
			public void run() {
				queue.remove(item);
				runnable.run();
			}
		});
		queue.offer(item);
		return item.disposable;
	}

	public IDisposable schedule(final Runnable runnable, long initialDelay, final long delay, final TimeUnit unit) {
		return schedule(new Runnable() { 
			public void run() { 
				runnable.run();
				schedule(runnable, delay, unit);
			}
		}, initialDelay, unit);	
	}

	private ScheduledItem getNext() {
		while(queue.size() > 0) {
			ScheduledItem item = queue.peek();
			if(item.isCancelled)
				queue.poll();
			else
				return item;
		}
		return null;
	}

	public long now() {
		return now;
	}
}
