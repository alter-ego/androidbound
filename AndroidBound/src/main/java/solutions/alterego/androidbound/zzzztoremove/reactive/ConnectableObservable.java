package solutions.alterego.androidbound.zzzztoremove.reactive;

public class ConnectableObservable<T> implements IObservable<T> {
	private ISubject<T> subject;
	private IObservable<T> source;
	private IDisposable subscription;
	private boolean hasSubscription;

	public ConnectableObservable(IObservable<T> source, ISubject<T> subject) {
		this.subject = subject;
		this.source = source;
	}
	
	public IDisposable connect() {
		final IDisposable sourceSubscription; 
		
		if(!hasSubscription) {
			hasSubscription = true;
			sourceSubscription = source.subscribe(subject);			
			subscription = new IDisposable() { 
				public void dispose() {
					hasSubscription = false;
					sourceSubscription.dispose();
				}
			};
		}
		return subscription;
	}	
	
	public IDisposable subscribe(IObserver<T> observer) {
		return subject.subscribe(observer);
	}	
	
	public Observable<T> refCount() { 
		return new Observable<T>(new Func<IObserver<T>, IDisposable>() {
			int count = 0;
			IDisposable connectableSubscription;
			
			public IDisposable invoke(IObserver<T> observer) {
				count++;
				final IDisposable subscription = subscribe(observer);
				if(count == 1)
					connectableSubscription = connect();
				return new IDisposable() { 
					public void dispose() { 
						subscription.dispose();
						count--;
						if(count == 0)
							connectableSubscription.dispose();
					}
				};
			}
		});
	}
}
