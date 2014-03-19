package com.alterego.androidbound.helpers.reflector;

import java.lang.reflect.Constructor;

public class ConstructorInfo {

    public final Constructor<?> mConstructorOriginal;
    public final String mConstructorName;
    public final int mConstructorParameterCount;
    public final Class<?>[] mConstructorParameterTypes;

    public ConstructorInfo(Constructor<?> constructor) {
        mConstructorOriginal = constructor;
        mConstructorName = constructor.getName();
        mConstructorParameterTypes = constructor.getParameterTypes();
        mConstructorParameterCount = constructor.getParameterTypes().length;
    }


}
