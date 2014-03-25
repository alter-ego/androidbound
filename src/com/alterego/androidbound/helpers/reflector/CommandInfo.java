package com.alterego.androidbound.helpers.reflector;


import java.lang.reflect.InvocationTargetException;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(prefix="m")
public class CommandInfo {

    @Getter public final String mCommandName;
    @Getter public final boolean mInvokerHasParameter;
    @Getter public final Class<?> mInvokerParameterType;
    @Getter public final boolean mCheckerHasParameter;
    @Getter public final Class<?> mCheckerParameterType;
    @Getter public final MethodInfo mInvokerMethod;
    @Getter public final MethodInfo mCheckerMethod;

    public CommandInfo(String name, Class<?> invokerParameterType,
                       Class<?> checkerParameterType, MethodInfo invoker, MethodInfo checker) {
        this.mCommandName = name;
        mInvokerParameterType = invokerParameterType;
        mCheckerParameterType = checkerParameterType;
        mInvokerHasParameter = mInvokerParameterType != null;
        mCheckerHasParameter = mCheckerParameterType != null;
        mInvokerMethod = invoker;
        mCheckerMethod = checker;
    }

    public boolean check(Object subject, Object parameter) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (mCheckerMethod != null) {
            if (mCheckerHasParameter) {
                return (Boolean) mCheckerMethod.getOriginalMethod().invoke(subject, parameter);
            } else {
                return (Boolean) mCheckerMethod.getOriginalMethod().invoke(subject);
            }
        }
        return true;
    }

    public void invoke(Object subject, Object parameter) throws IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        if (mInvokerMethod != null) {
            if (mInvokerHasParameter) {
                mInvokerMethod.getOriginalMethod().invoke(subject, parameter);
            } else {
                mInvokerMethod.getOriginalMethod().invoke(subject);
            }
        }
    }

}
