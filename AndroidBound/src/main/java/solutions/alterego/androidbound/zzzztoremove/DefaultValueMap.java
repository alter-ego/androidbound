package solutions.alterego.androidbound.zzzztoremove;

import java.util.HashMap;

public class DefaultValueMap<T, T1> extends HashMap<T, T1> {
	private static final long serialVersionUID = -9193286796321702989L;
	private T1 defaultValue;

	public DefaultValueMap() {
		this(null);
	}
	
	public DefaultValueMap(T1 defaultValue) {
		this.defaultValue = defaultValue;		
	}
	
	@Override
	public T1 get(Object key) {
		return containsKey(key) ? super.get(key) : defaultValue;
	}
}