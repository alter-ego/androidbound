package solutions.alterego.androidbound.utils;

import lombok.ToString;

@ToString
public abstract class Exceptional<T> {
    private Exceptional() {
    }

    public abstract boolean hasValue();

    public abstract T value();

    public abstract Throwable exception();

    @ToString
    private static final class Right<T> extends Exceptional<T> {
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((value == null) ? 0 : value.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Right other = (Right) obj;
            if (value == null) {
                if (other.value != null)
                    return false;
            } else if (!value.equals(other.value))
                return false;
            return true;
        }

        private final T value;

        Right(final T value) {
            this.value = value;
        }

        public boolean hasValue() {
            return true;
        }

        public T value() {
            return value;
        }

        public Throwable exception() {
            return null;
        }
    }

    @ToString
    private static final class Wrong<T> extends Exceptional<T> {
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((exception == null) ? 0 : exception.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            Wrong other = (Wrong) obj;
            if (exception == null) {
                if (other.exception != null)
                    return false;
            } else if (!exception.equals(other.exception))
                return false;
            return true;
        }

        private final Throwable exception;

        Wrong(final Throwable exception) {
            this.exception = exception;
        }

        public boolean hasValue() {
            return false;
        }

        public T value() {
            return null;
        }

        public Throwable exception() {
            return exception;
        }
    }

    public static <T> Exceptional<T> right(T value) {
        return new Right<T>(value);
    }

    public static <T> Exceptional<T> wrong(Throwable exception) {
        return new Wrong<T>(exception);
    }

}