package com.alterego.androidbound.zzzztoremove;

import java.util.concurrent.TimeUnit;

import com.alterego.androidbound.interfaces.INeedsLogger;
import com.alterego.androidbound.interfaces.IParser;
import com.alterego.androidbound.zzzztoremove.reactive.Func;
import com.alterego.androidbound.zzzztoremove.reactive.IDisposable;
import com.alterego.androidbound.zzzztoremove.reactive.IObservable;
import com.alterego.androidbound.zzzztoremove.reactive.IObserver;
import com.alterego.androidbound.zzzztoremove.reactive.IScheduler;
import com.alterego.androidbound.zzzztoremove.reactive.Observables;
import com.alterego.androidbound.zzzztoremove.reactive.Predicate;
import com.alterego.androidbound.zzzztoremove.reactive.ThreadPoolScheduler;

public class PollDataProvider<T> implements IObservable<T>, INeedsLogger {
	private IObservable<T> objects;
	private IContentProvider<String> provider;
	private IParser<T> parser;
	private String location;
	private ILogger logger = NullLogger.instance;

	public PollDataProvider(String location, long pollInterval, TimeUnit unit, IContentProvider<String> provider, IParser<T> parser) {
		this(location, pollInterval, unit, provider, parser, ThreadPoolScheduler.instance);
	}
	public PollDataProvider(String location, long pollInterval, TimeUnit unit, IContentProvider<String> provider, IParser<T> parser, IScheduler scheduler) {
		this.provider = provider;
		this.parser = parser;
		this.location = location;
		
		objects = Observables.every(pollInterval, unit, scheduler)
			.select(jsonFromLocation())
			.where(hasValue())
			.select(parsedObjectFromJson())
			.distinctUntilChanged()
			.takeUntil(isRunning())
			.replay(1)
			.refCount();
	}

	private Predicate<T> isRunning() {
		return new Predicate<T>() { 
			public Boolean invoke(T obj) {
				Boolean running = isRunning(obj);
				if(!running)
					logger.verbose("Sequence is completed for " + location + ". Polling stopped.");
				return running;
			}
		};
	}

	private Func<Long, Exceptional<String>> jsonFromLocation() {
		return new Func<Long, Exceptional<String>>() {
			public Exceptional<String> invoke(Long obj) {
				return provider.getContent(location);
			}
		};
	}
	
	private Predicate<Exceptional<String>> hasValue() { 
		return new Predicate<Exceptional<String>>() { 
			public Boolean invoke(Exceptional<String> obj) {
				if(!obj.hasValue())
					logger.info("Object requested for " + location  + " has no value: " + obj.exception().getMessage());
				return obj.hasValue();
			}
		};
	}
	
	private Func<Exceptional<String>, T> parsedObjectFromJson() {
		return new Func<Exceptional<String>, T>() {
			public T invoke(Exceptional<String> obj) {
				return parser.parse(obj.value());
			}
		};
	}

	protected boolean isRunning(T obj) {
		return true;
	}

	public IDisposable subscribe(IObserver<T> observer) {
		return objects.subscribe(observer);
	}

	@Override
	public void setLogger(ILogger logger) {
		this.logger  = logger.getLogger(this);
	}
}
