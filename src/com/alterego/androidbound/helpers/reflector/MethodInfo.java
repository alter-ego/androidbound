package com.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class MethodInfo {

    private final Method mOriginalMethod;

    private final String mMethodName;

    private final int mMethodParameterCount;

    private final Class<?>[] mMethodParameterTypes;

    private final Class<?> mMethodReturnType;

    public MethodInfo(Method method) {
        mOriginalMethod = method;
        mMethodName = method.getName();
        mMethodParameterTypes = method.getParameterTypes();
        mMethodParameterCount = method.getParameterTypes().length;
        mMethodReturnType = method.getReturnType();
    }
}
