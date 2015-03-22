package solutions.alterego.androidbound.zzzztoremove.reactive;

import java.lang.reflect.Method;

import android.util.Log;

public class Actions {
	public final static <T> Action<T> doNothing() {
		return new Action<T>() { public void invoke(T obj) { } };
	}
	
	public final static VoidAction doNothingVoid() {
		return new VoidAction() { public void invoke() {  } };
	}

	private static Method getMethod(Object instance, String methodName) {
		Method[] methods = instance.getClass().getDeclaredMethods();
		for(final Method method : methods) 
			if(method.getName().equals(methodName)) {
				if(!method.isAccessible()) 
				      method.setAccessible(true);
				return method;
			}
		return null;
	}
	
	public final static <T> Action<T> fromMethod(final Object instance, final String methodName) {
		final Method toCall = getMethod(instance, methodName);
		if(toCall != null)
			return new Action<T>() { 
				public void invoke(T obj) {
					try {
						toCall.invoke(instance, obj);
					} catch (Exception e) { 
						Log.e("Actions", "Error while invoking " + methodName + ": " + e.toString());
					}
				}
			};
		return doNothing();
	}
	
	public final static VoidAction fromVoidMethod(final Object instance, String methodName) {
		final Method toCall = getMethod(instance, methodName);
		if(toCall != null)
			return new VoidAction() { 
			public void invoke() {
				try {
					toCall.invoke(instance);
				} catch(Exception e) { }
			}
		};
		return doNothingVoid();
	}
}