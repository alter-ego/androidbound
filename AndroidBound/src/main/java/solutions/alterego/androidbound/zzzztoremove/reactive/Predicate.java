package solutions.alterego.androidbound.zzzztoremove.reactive;

import rx.functions.Func1;

public interface Predicate<T> extends Func1<T, Boolean> {

    static final Predicate<?> alwaysFalse = new Predicate<Object>() {
        public Boolean call(Object obj) {
            return false;
        }
    };
    static final Predicate<?> alwaysTrue = new Predicate<Object>() {
        public Boolean call(Object obj) {
            return true;
        }
    };
}