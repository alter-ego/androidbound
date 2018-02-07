package solutions.alterego.androidbound.helpers.reflector;

import android.util.Log;

import java.lang.reflect.Method;

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

    public Method getOriginalMethod() {
        return this.mOriginalMethod;
    }

    public String getMethodName() {
        return this.mMethodName;
    }

    public int getMethodParameterCount() {
        return this.mMethodParameterCount;
    }

    public Class<?>[] getMethodParameterTypes() {
        return this.mMethodParameterTypes;
    }

    public Class<?> getMethodReturnType() {
        return this.mMethodReturnType;
    }

    public void setOriginalMethod(Method mOriginalMethod) {
        this.mOriginalMethod = mOriginalMethod;
    }

    public void setMethodName(String mMethodName) {
        this.mMethodName = mMethodName;
    }

    public void setMethodParameterCount(int mMethodParameterCount) {
        this.mMethodParameterCount = mMethodParameterCount;
    }

    public void setMethodParameterTypes(Class<?>[] mMethodParameterTypes) {
        this.mMethodParameterTypes = mMethodParameterTypes;
    }

    public void setMethodReturnType(Class<?> mMethodReturnType) {
        this.mMethodReturnType = mMethodReturnType;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof MethodInfo)) {
            return false;
        }
        final MethodInfo other = (MethodInfo) o;
        if (!other.canEqual((Object) this)) {
            return false;
        }
        final Object this$mOriginalMethod = this.getOriginalMethod();
        final Object other$mOriginalMethod = other.getOriginalMethod();
        if (this$mOriginalMethod == null ? other$mOriginalMethod != null : !this$mOriginalMethod.equals(other$mOriginalMethod)) {
            return false;
        }
        final Object this$mMethodName = this.getMethodName();
        final Object other$mMethodName = other.getMethodName();
        if (this$mMethodName == null ? other$mMethodName != null : !this$mMethodName.equals(other$mMethodName)) {
            return false;
        }
        if (this.getMethodParameterCount() != other.getMethodParameterCount()) {
            return false;
        }
        if (!java.util.Arrays.deepEquals(this.getMethodParameterTypes(), other.getMethodParameterTypes())) {
            return false;
        }
        final Object this$mMethodReturnType = this.getMethodReturnType();
        final Object other$mMethodReturnType = other.getMethodReturnType();
        if (this$mMethodReturnType == null ? other$mMethodReturnType != null : !this$mMethodReturnType.equals(other$mMethodReturnType)) {
            return false;
        }
        return true;
    }

    public int hashCode() {
        final int PRIME = 59;
        int result = 1;
        final Object $mOriginalMethod = this.getOriginalMethod();
        result = result * PRIME + ($mOriginalMethod == null ? 43 : $mOriginalMethod.hashCode());
        final Object $mMethodName = this.getMethodName();
        result = result * PRIME + ($mMethodName == null ? 43 : $mMethodName.hashCode());
        result = result * PRIME + this.getMethodParameterCount();
        result = result * PRIME + java.util.Arrays.deepHashCode(this.getMethodParameterTypes());
        final Object $mMethodReturnType = this.getMethodReturnType();
        result = result * PRIME + ($mMethodReturnType == null ? 43 : $mMethodReturnType.hashCode());
        return result;
    }

    protected boolean canEqual(Object other) {
        return other instanceof MethodInfo;
    }

    public String toString() {
        return "MethodInfo(mOriginalMethod=" + this.getOriginalMethod() + ", mMethodName=" + this.getMethodName() + ", mMethodParameterCount=" + this
                .getMethodParameterCount() + ", mMethodParameterTypes=" + java.util.Arrays.deepToString(this.getMethodParameterTypes())
                + ", mMethodReturnType=" + this.getMethodReturnType() + ")";
    }
}
