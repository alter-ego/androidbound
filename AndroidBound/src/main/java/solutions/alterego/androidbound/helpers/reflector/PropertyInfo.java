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

    public PropertyInfo(String name, boolean canRead, boolean canWrite, Class<?> type, MethodInfo getter, MethodInfo setter, FieldInfo field,
            ILogger logger) {
        mPropertyType = type;
        mPropertyName = name;
        mCanRead = canRead;
        mCanWrite = canWrite;
        mGetterMethod = getter;
        mSetterMethod = setter;
        mField = field;
        mLogger = logger;
    }

    public Object getValue(Object obj) {
        Object result = null;
        if (mGetterMethod != null || mField != null) {
            try {
                result = mGetterMethod != null ? mGetterMethod.getOriginalMethod().invoke(obj)
                        : (mField != null ? mField.getFieldOriginal().get(obj) : null);
            } catch (Exception e) {
                mLogger.error("PropertyInfo getValue exception = " + e.getCause().toString());
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
        if (mSetterMethod != null || mField != null) {
            try {
                if (mSetterMethod != null) {
                    mSetterMethod.getOriginalMethod().invoke(obj, value);
                } else if (mField != null) {
                    mField.getFieldOriginal().set(obj, value);
                }
            } catch (Exception e) {
            }
        } else if (obj != null && obj instanceof Map) {
            ((Map) obj).put(mPropertyName, value);
        }
    }
}
