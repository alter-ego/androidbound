package solutions.alterego.androidbound.helpers.reflector;


import java.util.Map;

import lombok.Getter;
import lombok.experimental.Accessors;
import solutions.alterego.androidbound.interfaces.ILogger;

@Accessors(prefix = "m")
public class PropertyInfo {

    @Getter
    public final Class<?> mPropertyType;

    @Getter
    public final String mPropertyName;

    @Getter
    public final boolean mCanRead;

    @Getter
    public final boolean mCanWrite;

    @Getter
    private final FieldInfo mField;

    @Getter
    private final MethodInfo mGetterMethod;

    @Getter
    private final MethodInfo mSetterMethod;

    private final ILogger mLogger;

    @Getter
    private final MethodInfo mAdder;

    @Getter
    private final boolean mCanAdd;

    @Getter
    private final boolean mCanRemove;

    private final MethodInfo mRemover;

    public PropertyInfo(String name, boolean canRead, boolean canWrite, boolean canAdd, boolean canRemove, Class<?> type,
            MethodInfo getter, MethodInfo setter, MethodInfo adder, MethodInfo remover, FieldInfo field, ILogger logger) {
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
    }

    public Object getValue(Object obj) {
        Object result = null;
        if (mGetterMethod != null || mField != null) {
            try {
                result = mGetterMethod != null ? mGetterMethod.getOriginalMethod().invoke(obj) : mField.getFieldOriginal().get(obj);
            } catch (Exception e) {
                mLogger.error("PropertyInfo getValue exception = " + e.getCause().toString() + " for object = " + obj);
            }
        } else if (obj != null && obj instanceof Map) {
            result = ((Map) obj).get(mPropertyName);
        }
        if (result == null) {
            mLogger.warning("PropertyInfo getValue returns null");
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
            }
        } else if (mField != null) {
            try {
                mLogger.verbose(
                        "PropertyInfo setValue value = " + value + " for object = " + obj + " using field = "
                                + mField.getFieldOriginal().getName());
                mField.getFieldOriginal().set(obj, value);
            } catch (Exception e) {
                mLogger.warning("PropertyInfo couldn't setValue using property, value = " + value + " for object = " + obj);
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
            e.printStackTrace();
        }
    }

    public void removeValue(Object src, Object dst) {
        try {
            if (mRemover != null) {
                mRemover.getOriginalMethod().invoke(src, dst);
            } else {
                mLogger.verbose(
                        "PropertyInfo addValue value = " + dst + " for object = " + src + " using field = "
                                + " can't be invoked on a field.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
