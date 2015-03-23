package solutions.alterego.androidbound.zzzztoremove.reactive;

import android.util.Log;

import java.lang.reflect.Method;

import rx.functions.Action1;

public class Actions {

    public final static <T> Action1<T> doNothing() {
        return obj -> {
        };
    }

    public final static VoidAction doNothingVoid() {
        return new VoidAction() {
            public void invoke() {
            }
        };
    }

    private static Method getMethod(Object instance, String methodName) {
        Method[] methods = instance.getClass().getDeclaredMethods();
        for (final Method method : methods) {
            if (method.getName().equals(methodName)) {
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                return method;
            }
        }
        return null;
    }

    public final static <T> Action1<T> fromMethod(final Object instance, final String methodName) {
        final Method toCall = getMethod(instance, methodName);
        if (toCall != null) {
            return new Action1<T>() {
                public void call(T obj) {
                    try {
                        toCall.invoke(instance, obj);
                    } catch (Exception e) {
                        Log.e("Actions", "Error while invoking " + methodName + ": " + e.toString());
                    }
                }
            };
        }
        return doNothing();
    }

    public final static VoidAction fromVoidMethod(final Object instance, String methodName) {
        final Method toCall = getMethod(instance, methodName);
        if (toCall != null) {
            return new VoidAction() {
                public void invoke() {
                    try {
                        toCall.invoke(instance);
                    } catch (Exception e) {
                    }
                }
            };
        }
        return doNothingVoid();
    }
}