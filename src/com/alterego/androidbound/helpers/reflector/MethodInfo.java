package com.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Method;

public class MethodInfo {

    public final Method mOriginalMethod;
    public final String mMethodName;
    public final int mMethodParameterCount;
    public final Class<?>[] mMethodParameterTypes;
    public final Class<?> mMethodReturnType;

    public MethodInfo(Method method) {
        mOriginalMethod = method;
        mMethodName = method.getName();
        mMethodParameterTypes = method.getParameterTypes();
        mMethodParameterCount = method.getParameterTypes().length;
        mMethodReturnType = method.getReturnType();
    }

}
