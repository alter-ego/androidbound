package solutions.alterego.androidbound.helpers.reflector;


import java.util.Map;

import solutions.alterego.androidbound.interfaces.ILogger;

public class PropertyInfo {

    public final Class<?> mPropertyType;

    public final String mPropertyName;

    public final boolean mCanRead;

    public final boolean mCanWrite;

    private final FieldInfo mField;

    private final MethodInfo mGetterMethod;

    private final MethodInfo mSetterMethod;

    private final ILogger mLogger;

    private final boolean mDebugMode;

    private final MethodInfo mAdder;

    private final boolean mCanAdd;

    private final boolean mCanRemove;

    private final MethodInfo mRemover;

    public PropertyInfo(String name, boolean canRead, boolean canWrite, boolean canAdd, boolean canRemove, Class<?> type,
            MethodInfo getter, MethodInfo setter, MethodInfo adder, MethodInfo remover, FieldInfo field, ILogger logger, boolean debugMode) {
        mPropertyType = type;
        mPropertyName = name;
        mCanRead = canRead;
        mCanWrite = canWrite;
        mCanAdd = canAdd;
        mCanRemove = canRemove;
        mGetterMethod = getter;
        mSetterMethod = setter;
        mRemover = remover;
        mField = field;
        mAdder = adder;
        mLogger = logger;
        mDebugMode = debugMode;
    }

    public Object getValue(Object obj) {
        Object result = null;
        if (mGetterMethod != null || mField != null) {
            try {
                result = mGetterMethod != null ? mGetterMethod.getOriginalMethod().invoke(obj) : mField.getFieldOriginal().get(obj);
            } catch (Exception e) {
                mLogger.error("PropertyInfo getValue exception = " + e.getCause().toString() + " for object = " + obj);
                if (mDebugMode) {
                    throw new RuntimeException(e);
                }
            }
        } else if (obj != null && obj instanceof Map) {
            result = ((Map) obj).get(mPropertyName);
        }
        if (result == null) {
            String msg = "PropertyInfo getValue returns null";
            mLogger.warning(msg);
            if (mDebugMode) {
                throw new RuntimeException(msg);
            }
        }
        return result;
    }

    public void setValue(Object obj, Object value) {
        if (mSetterMethod != null) {
            try {
                mLogger.verbose(
                        "PropertyInfo setValue value = " + value + " for object = " + obj + " using method = "
                                + mSetterMethod.getOriginalMethod().getName());
                mSetterMethod.getOriginalMethod().invoke(obj, value);
            } catch (Exception e) {
                mLogger.warning("PropertyInfo couldn't setValue using method, value = " + value + " for object = " + obj);
                if (mDebugMode) {
                    throw new RuntimeException(e);
                }
            }
        } else if (mField != null) {
            try {
                mLogger.verbose(
                        "PropertyInfo setValue value = " + value + " for object = " + obj + " using field = "
                                + mField.getFieldOriginal().getName());
                mField.getFieldOriginal().set(obj, value);
            } catch (Exception e) {
                mLogger.warning("PropertyInfo couldn't setValue using property, value = " + value + " for object = " + obj);
                if (mDebugMode) {
                    throw new RuntimeException(e);
                }
            }
        } else if (obj != null && obj instanceof Map) { //using local map of values
            ((Map) obj).put(mPropertyName, value);
        }
    }

    public void addValue(Object src, Object dst) {
        try {
            if (mAdder != null) {
                mAdder.getOriginalMethod().invoke(src, dst);
            } else {
                mLogger.verbose(
                        "PropertyInfo addValue value = " + dst + " for object = " + src + " using field = "
                                + " can't be invoked on a field.");
            }
        } catch (Exception e) {
            mLogger.error("PropertyInfo couldn't addValue using property, value = " + dst + " for object = " + src);
            if (mDebugMode) {
                throw new RuntimeException(e);
            }
        }
    }

    public void removeValue(Object src, Object dst) {
        try {
            if (mRemover != null) {
                mRemover.getOriginalMethod().invoke(src, dst);
            } else {
                mLogger.verbose(
                        "PropertyInfo removeValue value = " + dst + " for object = " + src + " using field = "
                                + " can't be invoked on a field.");
            }
        } catch (Exception e) {
            mLogger.error("PropertyInfo couldn't removeValue using property, value = " + dst + " for object = " + src);
            if (mDebugMode) {
                throw new RuntimeException(e);
            }
        }
    }

    public Class<?> getPropertyType() {
        return this.mPropertyType;
    }

    public String getPropertyName() {
        return this.mPropertyName;
    }

    public boolean isCanRead() {
        return this.mCanRead;
    }

    public boolean isCanWrite() {
        return this.mCanWrite;
    }

    public FieldInfo getField() {
        return this.mField;
    }

    public MethodInfo getGetterMethod() {
        return this.mGetterMethod;
    }

    public MethodInfo getSetterMethod() {
        return this.mSetterMethod;
    }

    public MethodInfo getAdder() {
        return this.mAdder;
    }

    public boolean isCanAdd() {
        return this.mCanAdd;
    }

    public boolean isCanRemove() {
        return this.mCanRemove;
    }
}
