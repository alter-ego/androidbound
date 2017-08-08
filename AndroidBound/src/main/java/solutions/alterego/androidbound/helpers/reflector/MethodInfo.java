package solutions.alterego.androidbound.helpers.reflector;

import android.util.Log;

import java.lang.reflect.Method;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(prefix = "m")
public class MethodInfo {

    public static MethodInfo EMPTY = new MethodInfo();

    private Method mOriginalMethod;

    private String mMethodName;

    private int mMethodParameterCount;

    private Class<?>[] mMethodParameterTypes;

    private Class<?> mMethodReturnType;

    private MethodInfo() {
    }

    public static MethodInfo getMethodInfo(Method method) {
        MethodInfo info = new MethodInfo();

        try {
            info.mOriginalMethod = method;
            info.mMethodName = method.getName();
            info.mMethodParameterTypes = method.getParameterTypes();
            info.mMethodParameterCount = method.getParameterTypes().length;
            info.mMethodReturnType = method.getReturnType();
        } catch (Throwable e) {
            Log.w("AndroidBound", "Error in MethodInfo while checking method: " + method + ", error: ", e);
            info = EMPTY;
        }

        return info;
    }

}
