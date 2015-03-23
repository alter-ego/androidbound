package solutions.alterego.androidbound.utils;

import java.util.ArrayList;
import java.util.List;

public class Tuple {

    private static class TupleBase extends ArrayList<Object> {

        private static final long serialVersionUID = 1L;

        public TupleBase(int size) {
            super(size);
            for (int i = 0; i < size; i++) {
                this.add(null);
            }
        }

        @SuppressWarnings("unchecked")
        public <T> T castGet(int idx) {
            Object retval = this.get(idx);
            if (retval == null) {
                return null;
            }

            return (T) retval;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof TupleBase)) {
                return super.equals(o);
            }

            TupleBase that = (TupleBase) o;

            if (this.size() != that.size()) {
                return false;
            }

            for (int i = 0; i < this.size(); i++) {
                Object a = this.get(i);
                Object b = that.get(i);
                if ((a == null) != (b == null)) {
                    return false;
                }

                if (a != null && !a.equals(b)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static class Unit<T> extends TupleBase {

        public Unit(T value) {
            super(1);
            this.set(0, value);
        }

        public Unit(List<Object> baseList) {
            super(1);
            for (int i = 0; i < baseList.size() && i < 1; i++) {
                this.set(i, baseList.get(i));
            }
        }

        public T getValue() {
            return super.<T>castGet(0);
        }
    }

    public static class Pair<T, K> extends TupleBase {

        public Pair(T first, K second) {
            super(2);
            this.set(0, first);
            this.set(1, second);
        }

        public Pair(List<Object> baseList) {
            super(2);
            for (int i = 0; i < baseList.size() && i < 2; i++) {
                this.set(i, baseList.get(i));
            }
        }

        public T getFirstValue() {
            return super.<T>castGet(0);
        }


        public K getSecondValue() {
            return super.<K>castGet(1);
        }
    }

    public static class Triplet<T, K, L> extends TupleBase {

        public Triplet(T first, K second, L third) {
            super(3);
            this.set(0, first);
            this.set(1, second);
            this.set(2, third);
        }

        public Triplet(List<Object> baseList) {
            super(3);
            for (int i = 0; i < baseList.size() && i < 3; i++) {
                this.set(i, baseList.get(i));
            }
        }

        public T getFirstValue() {
            return super.<T>castGet(0);
        }

        public K getSecondValue() {
            return super.<K>castGet(1);
        }

        public L getThirdValue() {
            return super.<L>castGet(2);
        }
    }
}
