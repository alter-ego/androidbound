package com.alterego.androidbound.zzzztoremove.reactive;

public class ScheduledItem implements Comparable<ScheduledItem> {
	public IDisposable disposable;
	public long dueTime;
	public boolean isCancelled;
	private Runnable runnable;
	private static Runnable empty = new Runnable() { public void run() { } };
	
	public ScheduledItem(Runnable runnable, long dueTime) {
		this.runnable = runnable;
		this.dueTime = dueTime;
		disposable = new IDisposable() { public void dispose() { isCancelled = true; } };
	}
	
	public ScheduledItem(long dueTime) {
		this(empty, dueTime);
	}
	
	public void setRunnable(Runnable runnable) {
		this.runnable = runnable;
	}
	
	public void invoke() {
		if(!isCancelled)
			runnable.run();
	}

	public int compareTo(ScheduledItem other) {
		 return Long.valueOf(this.dueTime).compareTo(Long.valueOf(other.dueTime));
	}
}
