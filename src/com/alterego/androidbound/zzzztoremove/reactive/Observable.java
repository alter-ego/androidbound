package com.alterego.androidbound.zzzztoremove.reactive;

import java.util.Comparator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.joda.time.Instant;

import android.os.Handler;

import com.alterego.androidbound.zzzztoremove.reactive.Tuple.Pair;


public class Observable<T> implements IObservable<T> {
	private Func<IObserver<T>, IDisposable> externalSubscribe;
	
	private static class Holder<T> {
		public void setValue(T value) {
			this.value = value;
		}
		
		public T getValue() {
			return this.value;
		}
		
		private T value;
	}
	
	public Observable(Func<IObserver<T>, IDisposable> func) {
		this.externalSubscribe = func;
	}
	
	public IDisposable subscribe(IObserver<T> observer) {
		final AutoDetachObserver<T> autoDetach = new AutoDetachObserver<T>(observer);
		if(CurrentThreadScheduler.instance.isScheduleRequired())
			CurrentThreadScheduler.instance.schedule(new Runnable() {
				public void run() {
					autoDetach.disposable = externalSubscribe.invoke(autoDetach);
				}
		 	});
		else
			autoDetach.disposable = externalSubscribe.invoke(autoDetach);				
		return autoDetach;
	}
	
	private IDisposable subscribe(Action<T> next, Action<Exception> error, VoidAction completed) {
		return subscribe(Observers.fromActions(next, error, completed));
	}
	
	public IDisposable subscribe(Object instance, String nextMethod, String errorMethod, String completeMethod) {
		return subscribe(Actions.<T>fromMethod(instance, nextMethod), Actions.<Exception>fromMethod(instance, errorMethod), Actions.fromVoidMethod(instance, completeMethod));
	}

	public IDisposable subscribe(Object instance, String nextMethod, String errorMethod) {
		return subscribe(Actions.<T>fromMethod(instance, nextMethod), Actions.<Exception>fromMethod(instance, errorMethod), Actions.doNothingVoid());
	}
	
	public IDisposable subscribe(Object instance, String nextMethod) {
		return subscribe(Actions.<T>fromMethod(instance, nextMethod), Actions.<Exception>doNothing(), Actions.doNothingVoid());	
	}
	
	public <K> Observable<Tuple.Pair<T, K>> combineLatest(final IObservable<K> second) {
		final Holder<T> lastT = new Holder<T>();
		final Holder<K> lastK = new Holder<K>();
		
		return 
			this.select(new Func<T, Tuple.Pair<T, K>> () {
				@Override
				public Tuple.Pair<T, K> invoke(T obj) {
					lastT.setValue(obj);
					
					return new Tuple.Pair<T, K>(lastT.getValue(), lastK.getValue());
				}
			})
			.merge(
					Observables.from(second)
					.select(new Func<K, Tuple.Pair<T, K>>() {
						@Override
						public Tuple.Pair<T, K> invoke(K obj) {
							lastK.setValue(obj);
							
							return new Tuple.Pair<T, K>(lastT.getValue(), lastK.getValue());
						}
					}));
	}
	
	public Observable<T> merge(final IObservable<T> source) {
		final IObservable<T> that = this;
		
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			@Override
			public IDisposable invoke(IObserver<T> target) {
				final ISubject<T> decouple = new Subject<T>();
				
				final IObserver<T> merged = new IObserver<T>() {
					private int completedCount = 0;
					@Override
					public void onNext(T obj) {
						decouple.onNext(obj);
					}
					@Override
					public void onError(Exception exc) {
						decouple.onError(exc);
					}

					@Override
					public void onCompleted() {
						completedCount++;
						if(completedCount > 1) {
							decouple.onCompleted();
						}
					}
				};
				
				IDisposable result = decouple.subscribe(target);
				
				that.subscribe(merged);
				source.subscribe(merged);
				
				return result;
			}
		});
	}

	public Observable<T> where(final Func<T, Boolean> selector) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					boolean dispatch;
					public void onNext(T obj) {
						try {
							dispatch = selector.invoke(obj);
						}
						catch(Exception exception) {
							observer.onError(exception);
							return;
						}
						if(dispatch)
							observer.onNext(obj);
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	public <TResult> Observable<TResult> select(final Func<T, TResult> selector) {
		return new Observable<TResult>(new Func<IObserver<TResult>, IDisposable>() {
			public IDisposable invoke(final IObserver<TResult> observer) {
				return subscribe(new IObserver<T>() {
					TResult transformed;
					public void onNext(T obj) {
						try {
							transformed = selector.invoke(obj);
						}
						catch(Exception exception) {
							observer.onError(exception);
							return;
						}
						observer.onNext(transformed);
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	public Observable<T> inflate(final Func<T, Iterable<T>> inflater) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {

					public void onNext(T obj) {
						try {
							for(T itm: inflater.invoke(obj)) {
								observer.onNext(itm);
							}
						} catch(Exception ex) {
							observer.onError(ex);
						}
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	
	public Observable<T> throttle(final long dueTime, final TimeUnit unit) {
		return throttle(false, dueTime, unit);
	}
	
	public Observable<T> throttle(final boolean startSuspending, final long dueTime, final TimeUnit unit) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					private Object lockObject = new Object();
					private long lastMillisTime = 0;
					private long thresholdMillis = unit.toMillis(dueTime);
					private IDisposable futureSchedule;
					private T lastValue;
					private boolean lastValueSet;
					
					private void clearSchedule() {
						if(this.futureSchedule != null) {
							this.futureSchedule.dispose();
							this.futureSchedule = null;
						}
					}
					
					private void schedule() {
						if(this.futureSchedule != null) {
							return;
						}
												
						this.futureSchedule = ThreadPoolScheduler.instance.schedule(new Runnable() { @Override public void run() {
							onNextInSchedule();
						}}, dueTime, unit);
					}
					
					public void onNextInSchedule() {
						boolean tocall = false;
						T tocallvalue = null;
						
						synchronized(this.lockObject) {
							if(!this.lastValueSet) {
								return;
							}
							
							tocall = true;
							tocallvalue = this.lastValue;
							
							this.lastValue = null;
							this.lastValueSet = false;
							this.lastMillisTime = Instant.now().getMillis();
							this.clearSchedule();
						}
						
						if(tocall) {
							observer.onNext(tocallvalue);
						}
					}
					
					public void onNext(T obj) {
						boolean tocall = false;
						T tocallvalue = null;
						
						synchronized(this.lockObject) {
							long curTime = Instant.now().getMillis();
							if(curTime - lastMillisTime < thresholdMillis) {
								this.lastValue = obj;
								this.lastValueSet = true;
								this.schedule();
							} else {
								this.clearSchedule();
								if(this.lastValueSet) {
									tocall = true;
									tocallvalue = this.lastValue;
									
									this.lastValue = obj;
									this.lastValueSet = true;
									this.lastMillisTime = Instant.now().getMillis();
									this.schedule();
								} else {
									if(startSuspending) {
										this.lastValue = obj;
										this.lastValueSet = true;
										this.lastMillisTime = Instant.now().getMillis();
										this.schedule();
									} else {
										tocall = true;
										tocallvalue = obj;
										
										this.lastMillisTime = Instant.now().getMillis();
									}
								}
							}
						}
						
						if(tocall) {
							observer.onNext(tocallvalue);
						}
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	

	public Observable<T> distinctUntilChanged() {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					T current;
					boolean hasCurrent;
					boolean isEqual;
					
					public void onNext(T obj) {
						if(hasCurrent)
						{
							try {
								isEqual = obj == null ? (obj == current) : (obj.equals(current));
							} catch(Exception exception) {
								observer.onError(exception);
								return;
							}
						}
						if(!hasCurrent || !isEqual)
						{
							hasCurrent = true;
							current = obj;
							observer.onNext(obj);
						}
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	public Observable<T> distinctUntilChanged(final Comparator<T> comparator) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					T current;
					boolean hasCurrent;
					boolean isEqual;
					
					public void onNext(T obj) {
						if(hasCurrent)
						{
							try {
								isEqual = obj == null ? (obj == current) : comparator.compare(obj, current) == 0;
							} catch(Exception exception) {
								observer.onError(exception);
								return;
							}
						}
						if(!hasCurrent || !isEqual)
						{
							hasCurrent = true;
							current = obj;
							observer.onNext(obj);
						}
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	public Observable<T> observeOn(final Handler handler) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					public void onNext(final T obj) {
						handler.post(new Runnable() {
							public void run() {
								observer.onNext(obj);
							}
						});
					}

					public void onError(final Exception exc) {
						handler.post(new Runnable() {
							public void run() {
								observer.onError(exc);
							}
						});
					}

					public void onCompleted() {
						handler.post(new Runnable() {
							public void run() {
								observer.onCompleted();
							}
						});
					}
				});
			}
		});
	}
	
	public Observable<T> observeOn(final IScheduler scheduler) { 
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(IObserver<T> observer) {
				return subscribe(new ScheduledObserver<T>(scheduler, observer));
			} 
		});
	}
	
	public ConnectableObservable<T> publish() { 
		return new ConnectableObservable<T>(this, new Subject<T>());
	}
	
	public ConnectableObservable<T> multicast(ISubject<T> subject) {
		return new ConnectableObservable<T>(this, subject);
	}
	
	public ConnectableObservable<T> replay(int bufferSize) {
		return multicast(new ReplaySubject<T>(bufferSize));
	}
	
	public ConnectableObservable<T> replay(int bufferSize, IScheduler scheduler) { 
		return multicast(new ReplaySubject<T>(bufferSize, scheduler));
	}
	
	public ConnectableObservable<T> replay() {
		return multicast(new ReplaySubject<T>());
	}
	
	public Observable<T> takeWhile(final Predicate<T> predicate) { 
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					boolean running = true;
					
					public void onNext(T obj) {
						try {
							running = predicate.invoke(obj);
						}
						catch(Exception exception) {
							observer.onError(exception);
							return;
						}
						if(running)
							observer.onNext(obj);
						else
							observer.onCompleted();
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}
	
	public Observable<T> takeUntil(final Predicate<T> predicate) { 
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				return subscribe(new IObserver<T>() {
					boolean running = true;
					
					public void onNext(T obj) {
						observer.onNext(obj);
						try {
							running = predicate.invoke(obj);
						}
						catch(Exception exception) {
							onError(exception);
							return;
						}
						if(!running)
							onCompleted();
					}

					public void onError(Exception exc) {
						observer.onError(exc);						
					}

					public void onCompleted() {
						observer.onCompleted();
					} 
				});
			}
		});
	}

	public Observable<T> startWith(final T... start) {
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			public IDisposable invoke(final IObserver<T> observer) {
				for(T item : start)
					observer.onNext(item);
				return subscribe(observer);
			}
		});
	}
	
	class AutoDetachObserver<T1> implements IObserver<T1>, IDisposable {
		private IObserver<T1> observer;
		public IDisposable disposable;
		private AtomicInteger isStopped;
		
		public AutoDetachObserver(IObserver<T1> observer) {
			isStopped = new AtomicInteger(0);
			this.observer = observer;
			disposable = new IDisposable() { public void dispose() { } };
		}
		
		public void onNext(T1 obj) {
			if(isStopped.get() == 0)
				observer.onNext(obj);
		}

		public void onError(Exception exc) {			
			if(isStopped.compareAndSet(0, 1)) {
				observer.onError(exc);
				disposable.dispose();
			}
		}

		public void onCompleted() {
			if(isStopped.compareAndSet(0,  1)) {
				observer.onCompleted();
				disposable.dispose();
			}
		}

		public void dispose() {
			isStopped.set(1);
			disposable.dispose();
		}
	}
}
