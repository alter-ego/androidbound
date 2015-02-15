package com.alterego.androidbound.zzzztoremove.reactive;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import com.alterego.androidbound.zzzztoremove.GroupedList;
import com.alterego.androidbound.zzzztoremove.GroupingList;

public class Iterables {
    public static interface ToLongResolver<T, S> {
        public abstract long convertPositionToLong(T position);

        public abstract long convertDeltaToLong(S delta);

        public abstract T convertLongToPosition(long position);
    }

    public static class NullIterator<T> implements Iterator<T> {
        @Override
        public boolean hasNext() {
            return false;
        }

        @Override
        public T next() {
            return null;
        }

        @Override
        public void remove() {
        }
    }

    public static class NullIterable<T> implements Iterable<T> {
        @Override
        public Iterator<T> iterator() {
            return new NullIterator<T>();
        }
    }

    public static class SequenceIterator<T, S> implements Iterator<T> {
        public SequenceIterator(T start, int count, S step, ToLongResolver<T, S> resolver) {
            this.startMsec = resolver.convertPositionToLong(start);
            this.stepMsec = resolver.convertDeltaToLong(step);
            this.endMsec = this.startMsec + stepMsec * count;
            this.currentMsec = this.startMsec;
            this.resolver = resolver;

            if (this.stepMsec == 0f) {
                // make one shot
                this.stepMsec = this.endMsec - this.startMsec + 1;
            }
        }

        @Override
        public boolean hasNext() {
            return this.currentMsec <= this.endMsec;
        }

        @Override
        public T next() {
            if (this.currentMsec > this.endMsec) {
                throw new NoSuchElementException();
            }

            T retval = resolver.convertLongToPosition(this.currentMsec);

            if (this.currentMsec <= this.endMsec) {
                this.currentMsec += this.stepMsec;
            }

            return retval;
        }

        @Override
        public void remove() {
            if (this.currentMsec > this.startMsec) {
                this.currentMsec -= this.stepMsec;
            }
        }

        private long startMsec;
        private long endMsec;
        private long stepMsec;
        private long currentMsec;
        private ToLongResolver<T, S> resolver;
    }

    public static class SkipIterator<T> implements Iterator<T> {
        private boolean skipped;
        private int skipCount;
        private Iterator<T> baseIterator;

        public SkipIterator(Iterator<T> baseIterator, int skipCount) {
            this.baseIterator = baseIterator;
            this.skipCount = skipCount;
            this.skipped = false;
        }

        @Override
        public boolean hasNext() {
            if (!skipped) {
                skip(this.baseIterator, this.skipCount);
                this.skipped = true;
            }

            return this.baseIterator.hasNext();
        }

        @Override
        public T next() {
            if (!skipped) {
                skip(this.baseIterator, this.skipCount);
                this.skipped = true;
            }

            return this.baseIterator.next();
        }

        @Override
        public void remove() {
            if (!skipped) {
                return;
            }

            this.baseIterator.remove();
        }

        private static <T> Iterator<T> skip(Iterator<T> it, int cnt) {
            if (cnt <= 0) {
                return it;
            }

            int i = 0;
            while (i < cnt && it.hasNext()) {
                it.next();
                i++;
            }

            return it;
        }
    }

    public static class TakeIterator<T> implements Iterator<T> {
        private int takeCount;
        Iterator<T> baseIterator;
        private int curCount;

        public TakeIterator(Iterator<T> baseIterator, int takeCount) {
            this.baseIterator = baseIterator;
            this.takeCount = takeCount;

            if (this.takeCount < 0) {
                this.takeCount = 0;
            }
        }

        @Override
        public boolean hasNext() {
            if (this.curCount >= this.takeCount) {
                return false;
            }

            return this.baseIterator.hasNext();
        }

        @Override
        public T next() {
            if (this.curCount >= this.takeCount) {
                throw new NoSuchElementException();
            }

            if (!this.baseIterator.hasNext()) {
                throw new NoSuchElementException();
            }

            this.curCount++;

            return this.baseIterator.next();
        }

        @Override
        public void remove() {
            if (this.curCount > 0) {
                this.curCount--;
                this.baseIterator.remove();
            }
        }
    }

    public static class WhereIterator<T> implements Iterator<T> {
        private Iterator<T> baseIterator;
        private Predicate<T> filter;
        private T lastValue;
        private boolean lastValueAdvanced;

        public WhereIterator(Iterator<T> baseIterator, Predicate<T> filter) {
            this.baseIterator = baseIterator;
            this.filter = filter;
            this.lastValueAdvanced = true;
        }

        public boolean hasNext() {
            return this.nextValue(false);
        }

        public T next() {
            if (!this.nextValue(true)) {
                throw new NoSuchElementException();
            }

            return this.lastValue;
        }

        public void remove() {
            // TODO something else, more functional
            throw new UnsupportedOperationException();
        }

        private boolean nextValue(boolean advance) {
            if (!this.lastValueAdvanced) {
                this.lastValueAdvanced = advance;
                return true;
            }

            this.lastValueAdvanced = advance;
            this.lastValue = null;

            boolean hasvalue = false;
            while (this.baseIterator.hasNext()) {
                this.lastValue = this.baseIterator.next();
                if (this.filter.invoke(lastValue)) {
                    hasvalue = true;
                    break;
                }
                this.lastValue = null;
            }

            this.lastValueAdvanced = hasvalue ? this.lastValueAdvanced : false;

            return hasvalue;
        }
    }

    public static class WhileIterator<T> implements Iterator<T> {
        private Iterator<T> baseIterator;
        private Predicate<T> filter;
        private T lastValue;
        private boolean lastValueAdvanced;
        private boolean ended;

        public WhileIterator(Iterator<T> baseIterator, Predicate<T> filter) {
            this.baseIterator = baseIterator;
            this.filter = filter;
            this.lastValueAdvanced = true;
            this.ended = false;
        }

        public boolean hasNext() {
            return this.nextValue(false);
        }

        public T next() {
            if (!this.nextValue(true)) {
                throw new NoSuchElementException();
            }
            return this.lastValue;
        }

        public void remove() {
            // TODO something else, more functional
            throw new UnsupportedOperationException();
        }

        private boolean nextValue(boolean advance) {
            if (this.ended) {
                return false;
            }

            if (!this.lastValueAdvanced) {
                this.lastValueAdvanced = advance;
                return true;
            }

            this.lastValueAdvanced = advance;
            this.lastValue = null;

            if (this.baseIterator.hasNext()) {
                this.lastValue = this.baseIterator.next();
                this.lastValueAdvanced = this.lastValue != null ? this.lastValueAdvanced : false;
                if (!this.filter.invoke(lastValue)) {
                    this.ended = true;
                    this.lastValue = null;
                    this.lastValueAdvanced = true;
                }
            } else {
                this.ended = true;
                this.lastValue = null;
                this.lastValueAdvanced = true;
            }

            return !ended;
        }
    }

    public static class SampleIterator<T> implements Iterator<T> {
        private int sampleFactor;
        private Iterator<T> baseIterator;

        public SampleIterator(Iterator<T> baseIterator, int sampleFactor) {
            this.baseIterator = baseIterator;
            this.sampleFactor = sampleFactor;
            if (this.sampleFactor < 1) {
                this.sampleFactor = 1;
            }
        }

        @Override
        public boolean hasNext() {
            return this.baseIterator.hasNext();
        }

        @Override
        public T next() {
            T retval = this.baseIterator.next();

            skip(this.baseIterator, this.sampleFactor - 1);

            return retval;
        }

        @Override
        public void remove() {
            this.baseIterator.remove();
        }

        private static <T> Iterator<T> skip(Iterator<T> it, int cnt) {
            if (cnt <= 0) {
                return it;
            }

            int i = 0;
            while (i < cnt && it.hasNext()) {
                it.next();
                i++;
            }

            return it;
        }
    }

    private static class IteratorStore<T> {
        private T value;
        private boolean hasValue = false;

        public void set(T value) {
            this.value = value;
            hasValue = true;
        }

        public T remove() {
            T result = null;
            if (hasValue) {
                result = value;
                value = null;
                hasValue = false;
            }
            return result;
        }

        public boolean hasValue() {
            return this.hasValue;
        }
    }

    public abstract static class AnonymousIterable<T> implements Iterable<T> {
        public List<T> toList() {
            List<T> result = new ArrayList<T>();
            for (T item : this)
                result.add(item);
            return result;
        }

        public T[] toArray(Class<T> c) {
            List<T> list = toList();
            @SuppressWarnings("unchecked")
            T[] dummy = (T[]) Array.newInstance(c, list.size());
            return toList().toArray(dummy);
        }
    }

    public static class IterableBuilder<T> {
        private final Iterable<T> source;

        public IterableBuilder(Iterable<T> source) {
            if (source == null) {
                source = new ArrayList<T>();
            }
            this.source = source;
        }

        public AnonymousIterable<T> where(final Predicate<T> predicate) {
            return new AnonymousIterable<T>() {

                public Iterator<T> iterator() {
                    return new Iterator<T>() {
                        final Iterator<T> iterator = source.iterator();
                        IteratorStore<T> store = new IteratorStore<T>();

                        public boolean hasNext() {
                            if (store.hasValue())
                                return true;
                            while (iterator.hasNext()) {
                                T value = iterator.next();
                                if (predicate.invoke(value)) {
                                    store.set(value);
                                    return true;
                                }
                            }
                            return false;
                        }

                        public T next() {
                            if (hasNext())
                                return store.remove();
                            throw new NoSuchElementException();
                        }

                        public void remove() {
                            iterator.remove();
                        }
                    };
                }
            };
        }


        public <TKey> GroupingList<TKey, T> groupBy(final Func<T, TKey> keyFunction) {
            GroupingList<TKey, T> result = new GroupingList<TKey, T>();
            for (T item : source) {
                TKey key = keyFunction.invoke(item);
                if (result.get(key) == null)
                    result.add(new GroupedList<TKey, T>(key));
                result.get(key).add(item);
            }
            return result;
        }
    }

    public static <T> IterableBuilder<T> from(Iterable<T> source) {
        return new IterableBuilder<T>(source);
    }

    public static <T> IterableBuilder<T> from(T[] source) {
        return new IterableBuilder<T>(Arrays.asList(source));
    }


    public static class MonoidIterableBuilder<T> implements Iterable<T> {
        private final Iterable<T> source;

        public MonoidIterableBuilder(Iterable<T> source) {
            this.source = source;
        }

        public MonoidIterableBuilder<T> where(final Predicate<T> predicate) {
            final Iterable<T> baseSource = this.source;

            return new MonoidIterableBuilder<T>(new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new WhereIterator<T>(baseSource.iterator(), predicate);
                }
            });
        }

        public MonoidIterableBuilder<T> skip(final int count) {
            final Iterable<T> baseSource = this.source;

            return new MonoidIterableBuilder<T>(new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new SkipIterator<T>(baseSource.iterator(), count);
                }
            });
        }

        public MonoidIterableBuilder<T> take(final int count) {
            final Iterable<T> baseSource = this.source;

            return new MonoidIterableBuilder<T>(new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new TakeIterator<T>(baseSource.iterator(), count);
                }
            });
        }

        public MonoidIterableBuilder<T> takeWhile(final Predicate<T> predicate) {
            final Iterable<T> baseSource = this.source;

            return new MonoidIterableBuilder<T>(new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new WhileIterator<T>(baseSource.iterator(), predicate);
                }
            });
        }

        public MonoidIterableBuilder<T> sample(final int samplingSize) {
            final Iterable<T> baseSource = this.source;

            return new MonoidIterableBuilder<T>(new Iterable<T>() {
                @Override
                public Iterator<T> iterator() {
                    return new SampleIterator<T>(baseSource.iterator(), samplingSize);
                }
            });
        }

        public <TResult> MonoidIterableBuilder<TResult> select(final Func<T, TResult> selector) {
            return new MonoidIterableBuilder<TResult>(
                    new Iterable<TResult>() {
                        @Override
                        public Iterator<TResult> iterator() {
                            return new Iterator<TResult>() {
                                private Iterator<T> iter;

                                private Iterator<T> sourceIterator() {
                                    if (this.iter == null) {
                                        this.iter = source.iterator();
                                    }

                                    return this.iter;
                                }

                                public boolean hasNext() {
                                    return this.sourceIterator().hasNext();
                                }

                                public TResult next() {
                                    return selector.invoke(this.sourceIterator().next());
                                }

                                public void remove() {
                                    this.sourceIterator().remove();
                                }
                            };
                        }
                    });
        }

        public List<T> toList() {
            ArrayList<T> retval = new ArrayList<T>();
            Iterator<T> iter = source.iterator();
            while (iter.hasNext()) {
                retval.add(iter.next());
            }

            return retval;
        }

        public T firstOrDefault() {
            Iterator<T> iter = source.iterator();
            if (iter.hasNext()) {
                return iter.next();
            }

            return null;
        }

        public <TKey> GroupingList<TKey, T> groupBy(final Func<T, TKey> keyFunction) {
            GroupingList<TKey, T> result = new GroupingList<TKey, T>();
            Iterator<T> iter = source.iterator();
            while (iter.hasNext()) {
                T item = iter.next();
                TKey key = keyFunction.invoke(item);
                if (result.get(key) == null)
                    result.add(new GroupedList<TKey, T>(key));
                result.get(key).add(item);
            }

            return result;
        }

        @Override
        public Iterator<T> iterator() {
            return this.source.iterator();
        }
    }

    public static <T> MonoidIterableBuilder<T> monoFrom(Iterable<T> source) {
        return new MonoidIterableBuilder<T>(source);
    }

    public static <T> MonoidIterableBuilder<T> monoFrom(T[] source) {
        return new MonoidIterableBuilder<T>(Arrays.asList(source));
    }
}
