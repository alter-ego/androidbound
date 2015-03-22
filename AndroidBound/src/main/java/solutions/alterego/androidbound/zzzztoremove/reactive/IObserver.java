package solutions.alterego.androidbound.zzzztoremove.reactive;

public interface IObserver<T> {
	void onNext(T obj);
	void onError(Exception exc);
	void onCompleted();
}