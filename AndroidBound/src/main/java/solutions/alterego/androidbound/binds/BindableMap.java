package solutions.alterego.androidbound.binds;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import solutions.alterego.androidbound.interfaces.INotifyPropertyChanged;
import solutions.alterego.androidbound.zzzztoremove.reactive.IObservable;
import solutions.alterego.androidbound.zzzztoremove.reactive.ISubject;
import solutions.alterego.androidbound.zzzztoremove.reactive.Iterables;
import solutions.alterego.androidbound.zzzztoremove.reactive.Subject;

public class BindableMap<K, V> extends HashMap<K, V> implements INotifyPropertyChanged {

    private static final long serialVersionUID = 1L;

    protected ISubject<String> mPropertySubject;

    protected IValidator<K, V> mPropertyValidator;

    protected V mDefaultValue;

    public BindableMap() {
        this(null, null);
    }

    public BindableMap(IValidator<K, V> validator) {
        this(validator, null);
    }

    public BindableMap(IValidator<K, V> validator, V defaultValue) {
        mPropertyValidator = validator;
        mPropertySubject = new Subject<String>();
        mDefaultValue = defaultValue;
    }

    @Override
    public V put(K key, V value) {
        if (mPropertyValidator != null) {
            if (!mPropertyValidator.isValid(this, key, value)) {
                if (!mPropertyValidator.canMakeValid(this, key, value)) {
                    return this.get(key);
                }

                value = mPropertyValidator.makeValid(this, key, value);
            }
        }

        V oldValue = super.put(key, value);

        if (oldValue == value) {
            return oldValue;
        }

        if (oldValue != null && value != null && oldValue.equals(value)) {
            return oldValue;
        }

        if (key != null) {
            raisePropertyChanged(key.toString());
        }

        return oldValue;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> map) {
        if (map == null) {
            return;
        }

        for (Map.Entry<? extends K, ? extends V> ent : map.entrySet()) {
            put(ent.getKey(), ent.getValue());
        }
    }

    @Override
    public void clear() {
        List<K> oldKeys = Iterables.monoFrom(super.keySet()).toList();

        super.clear();

        for (K key : oldKeys) {
            if (key != null) {
                this.raisePropertyChanged(key.toString());
            }
        }
    }

    @Override
    public V remove(Object key) {
        boolean contains = super.containsKey(key);
        V oldValue = super.remove(key);

        if (contains && key != null) {
            this.raisePropertyChanged(key.toString());
        }

        return oldValue;
    }

    @Override
    public V get(Object key) {
        if (!super.containsKey(key)) {
            return mDefaultValue;
        }
        return super.get(key);
    }

    @Override
    public IObservable<String> onPropertyChanged() {
        return mPropertySubject;
    }

    public void forceValidation() {
        if (mPropertyValidator == null) {
            return;
        }

        Map<K, V> copy = new HashMap<K, V>(this);
        for (Map.Entry<K, V> ent : copy.entrySet()) {
            this.put(ent.getKey(), ent.getValue());
        }
    }

    @Override
    public void dispose() {
        if (mPropertySubject != null) {
            mPropertySubject.dispose();
            mPropertySubject = null;
        }

        super.clear();
    }

    protected void raisePropertyChanged(String property) {
        mPropertySubject.onNext(property);
    }

    public static interface IValidator<K, V> {

        public abstract boolean isValid(Map<K, V> map, K key, V value);

        public abstract boolean canMakeValid(Map<K, V> map, K key, V value);

        public abstract V makeValid(Map<K, V> map, K key, V value);
    }
}
