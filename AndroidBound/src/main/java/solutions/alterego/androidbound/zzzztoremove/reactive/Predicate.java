package solutions.alterego.androidbound.zzzztoremove.reactive;

public interface Predicate<T> extends Func<T, Boolean> {  
	static final Predicate<?> alwaysFalse = new Predicate<Object>() { public Boolean invoke(Object obj) { return false; } };
	static final Predicate<?> alwaysTrue = new Predicate<Object>() { public Boolean invoke(Object obj) { return true; } };
}