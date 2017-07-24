package solutions.alterego.androidbound.plugin;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class Java8Iterator {

    public static <E> void forEachRemaining(Iterator<E> iterator, Consumer<? super E> action) {
        Objects.requireNonNull(action);
        while (iterator.hasNext())
            action.accept(iterator.next());
    }

}
