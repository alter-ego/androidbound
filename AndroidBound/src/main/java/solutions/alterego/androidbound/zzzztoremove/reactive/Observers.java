package solutions.alterego.androidbound.zzzztoremove.reactive;


public final class Observers {
	private static class AnonymousObserver<T> implements IObserver<T>
	{
		Action<T> onNext;
		Action<Exception> onError;
		VoidAction onCompleted;
		
		public AnonymousObserver(Action<T> onNext, Action<Exception> onError, VoidAction onCompleted)
		{
			this.onNext = onNext;
			this.onError = onError;
			this.onCompleted = onCompleted;
		}
		
		public void onNext(T value) {
			onNext.invoke(value);
		}

		public void onError(Exception error) {
			onError.invoke(error);
		}

		public void onCompleted() {
			onCompleted.invoke();
		}
	}
	
	public final static <T> IObserver<T> fromAction(Action<T> onNext) {
		return new AnonymousObserver<T>(onNext, Actions.<Exception>doNothing(), Actions.doNothingVoid());
	}
	
	public final static <T> IObserver<T> fromAction(VoidAction onCompleted) {
		return new AnonymousObserver<T>(Actions.<T>doNothing(), Actions.<Exception>doNothing(), onCompleted);
	}
	
	public final static <T> IObserver<T> fromError(Action<Exception> onError) {
		return new AnonymousObserver<T>(Actions.<T>doNothing(), onError, Actions.doNothingVoid());
	}
	
	public final static <T> IObserver<T> fromActions(Action<T> onNext, VoidAction onCompleted) {
		return new AnonymousObserver<T>(onNext, Actions.<Exception>doNothing(), onCompleted);
	}

	public final static <T> IObserver<T> fromActions(Action<T> onNext, Action<Exception> onError) { 
		return new AnonymousObserver<T>(onNext, onError, Actions.doNothingVoid());
	}
	
	public final static <T> IObserver<T> fromActions(Action<T> onNext, Action<Exception> onError, VoidAction onCompleted) {
		return new AnonymousObserver<T>(onNext, onError, onCompleted);
	}
}
