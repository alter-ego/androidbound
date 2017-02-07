package solutions.alterego.androidbound.helpers;

import android.util.SparseArray;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;
import solutions.alterego.androidbound.helpers.reflector.CommandInfo;
import solutions.alterego.androidbound.helpers.reflector.ConstructorInfo;
import solutions.alterego.androidbound.helpers.reflector.FieldInfo;
import solutions.alterego.androidbound.helpers.reflector.MethodInfo;
import solutions.alterego.androidbound.helpers.reflector.PropertyInfo;
import solutions.alterego.androidbound.interfaces.ILogger;
import solutions.alterego.androidbound.utils.Iterables;

public class Reflector {

    private static final String COMMAND_PREFIX_DO = "do";

    private static final String COMMAND_PREFIX_CAN = "can";

    private static final String PROPERTY_PREFIX_GET = "get";

    private static final String PROPERTY_PREFIX_SET = "set";

    private static final String PROPERTY_PREFIX_IS = "is";

    private static final String PROPERTY_PREFIX_ADD = "add";

    private static final String PROPERTY_PREFIX_REMOVE = "remove";

    private static final Object mSynchronizedObject = new Object();

    private static SparseArray<SparseArray<PropertyInfo>> mObjectProperties
            = new SparseArray<SparseArray<PropertyInfo>>();

    private static SparseArray<SparseArray<CommandInfo>> mObjectCommands = new SparseArray<SparseArray<CommandInfo>>();

    private static SparseArray<SparseArray<List<MethodInfo>>> mObjectMethods
            = new SparseArray<SparseArray<List<MethodInfo>>>();

    private static SparseArray<List<ConstructorInfo>> mObjectConstructors = new SparseArray<List<ConstructorInfo>>();

    private static SparseArray<SparseArray<FieldInfo>> mObjectFields = new SparseArray<SparseArray<FieldInfo>>();

    public static boolean isCommand(Class<?> type, String name) {
        return Iterables.from(getMethods(type, COMMAND_PREFIX_DO + name))
                .filter(methodInfo -> methodInfo.getMethodParameterCount() <= 1)
                .iterator()
                .hasNext();
    }

    public static boolean isProperty(Class<?> type, String name) {
        if (Iterables.from(getMethods(type, PROPERTY_PREFIX_GET + name))
                .filter(methodInfo -> methodInfo.getMethodParameterCount() == 0)
                .iterator()
                .hasNext()) {
            return true;
        } else if (Iterables.from(getMethods(type, PROPERTY_PREFIX_IS + name))
                .filter(methodInfo -> methodInfo.getMethodParameterCount() == 0)
                .iterator()
                .hasNext()) {
            return true;
        } else {
            return false;
        }
    }

    public static PropertyInfo getProperty(Class<?> type, String name, ILogger logger) {
        MethodInfo propertyGetter = null;
        MethodInfo propertySetter = null;
        MethodInfo propertyAdd = null;
        MethodInfo propertyRemove = null;
        FieldInfo propertyField = null;

        int typeCode = type.hashCode();
        int nameCode = name.hashCode();

        PropertyInfo propertyInfo = null;
        synchronized (mSynchronizedObject) {
            SparseArray<PropertyInfo> objectProperties = mObjectProperties.get(typeCode);
            if (objectProperties != null) {
                propertyInfo = objectProperties.get(nameCode);
            }
        }

        if (propertyInfo != null) {
            return propertyInfo;
        }

        //first we look for getters with prefix "get", if null then with prefix "is"
        propertyGetter = findGetterWithGetPrefix(type, name);
        if (propertyGetter == null) {
            propertyGetter = findGetterWithIsPrefix(type, name);
        }

        if (propertyGetter == null) {
            propertyGetter = findGetterWithPrefix(type, PROPERTY_PREFIX_ADD, name);
        }

        if (propertyGetter == null) {
            propertyGetter = findGetterWithPrefix(type, PROPERTY_PREFIX_REMOVE, name);
        }

        //if the getter is not null, then we look for the setter; if it is null, we bind directly to the variable
        if (propertyGetter != null) {
            propertySetter = findSetter(type, name, propertyGetter);
            propertyAdd = findAddMethods(type, name, propertyGetter);
            propertyRemove = findRemoveMethods(type, name, propertyGetter);
        } else {
            propertyField = Reflector.getField(type, name);
        }

        boolean isMap = Map.class.isAssignableFrom(type);

        propertyInfo = new PropertyInfo(name,
                propertyGetter != null || propertyField != null || isMap,
                propertySetter != null || propertyField != null || isMap,
                propertyAdd != null,
                propertyRemove != null,
                propertyGetter != null ? propertyGetter.getMethodReturnType()
                        : (propertyField != null ? propertyField.getFieldType() : Object.class),
                propertyGetter,
                propertySetter,
                propertyAdd,
                propertyRemove,
                propertyField,
                logger);

        synchronized (mSynchronizedObject) {
            SparseArray<PropertyInfo> sa = mObjectProperties.get(typeCode);
            if (sa != null) {
                sa = cloneSparseArray(sa);
            } else {
                sa = new SparseArray<PropertyInfo>();
            }
            sa.put(nameCode, propertyInfo);
            mObjectProperties.put(typeCode, sa);
        }

        return propertyInfo;

    }

    private static MethodInfo findGetterWithIsPrefix(Class<?> type, String name) {
        return findGetterWithPrefix(type, PROPERTY_PREFIX_IS, name);
    }

    private static MethodInfo findGetterWithGetPrefix(Class<?> type, String name) {
        return findGetterWithPrefix(type, PROPERTY_PREFIX_GET, name);
    }

    private static MethodInfo findGetterWithPrefix(Class<?> type, String prefix, String name) {
        MethodInfo found_getter = null;
        List<MethodInfo> getters = getMethods(type, prefix + name);

        if (getters != null) {
            for (MethodInfo getter : getters) {
                if (getter.getMethodParameterCount() == 0) {
                    found_getter = getter;
                    break;
                }
            }
        }

        return found_getter;
    }

    private static MethodInfo findSetter(Class<?> type, String name, MethodInfo getter) {

        MethodInfo found_setter = null;
        List<MethodInfo> setters = getMethods(type, PROPERTY_PREFIX_SET + name);
        if (setters != null) {
            for (MethodInfo setter : setters) {
                if (setter.getMethodParameterCount() == 1 && getter.getMethodReturnType()
                        .equals(setter.getMethodParameterTypes()[0])) {
                    found_setter = setter;
                    break;
                }
            }
        }

        return found_setter;
    }


    private static MethodInfo findAddMethods(Class<?> type, String name, MethodInfo getter) {
        MethodInfo found_setter = null;
        List<MethodInfo> adders = getMethods(type, PROPERTY_PREFIX_ADD + name);
        if (adders != null) {
            for (MethodInfo setter : adders) {
                if (setter.getMethodParameterCount() == 1
                        && Collection.class.isAssignableFrom(setter.getMethodParameterTypes()[0])) {
                    found_setter = setter;
                    break;
                }
            }
        }
        return found_setter;
    }

    private static MethodInfo findRemoveMethods(Class<?> type, String name, MethodInfo getter) {
        MethodInfo found_setter = null;
        List<MethodInfo> adders = getMethods(type, PROPERTY_PREFIX_REMOVE + name);
        if (adders != null) {
            for (MethodInfo setter : adders) {
                if (setter.getMethodParameterCount() == 1
                        && Collection.class.isAssignableFrom(setter.getMethodParameterTypes()[0])) {
                    found_setter = setter;
                    break;
                }
            }
        }
        return found_setter;
    }


    public static CommandInfo getCommand(Class<?> type, String name) {
        int typeCode = type.hashCode();
        int nameCode = name.hashCode();

        CommandInfo retval = null;
        synchronized (mSynchronizedObject) {
            SparseArray<CommandInfo> sa = mObjectCommands.get(typeCode);
            if (sa != null) {
                retval = sa.get(nameCode);
            }
        }

        if (retval != null) {
            return retval;
        }

        MethodInfo invoker = null;
        MethodInfo checker = null;
        Class<?> invokerParameterType = null;
        Class<?> checkerParameterType = null;

        List<MethodInfo> invokers = getMethods(type, COMMAND_PREFIX_DO + name);
        if (invokers != null) {
            for (MethodInfo mi : invokers) {
                if (mi.getMethodParameterCount() <= 1) {
                    invoker = mi;
                    if (mi.getMethodParameterCount() > 0) {
                        invokerParameterType = mi.getMethodParameterTypes()[0];
                    }
                    break;
                }
            }
        }

        if (invoker != null) {
            List<MethodInfo> checkers = getMethods(type, COMMAND_PREFIX_CAN + name);
            if (checkers != null) {
                for (MethodInfo mi : checkers) {
                    if (mi.getMethodParameterCount() <= 1 && mi.getMethodReturnType() == boolean.class) {
                        checker = mi;
                        if (mi.getMethodParameterCount() > 0) {
                            checkerParameterType = mi.getMethodParameterTypes()[0];
                        }
                        break;
                    }
                }
            }
        }

        retval = new CommandInfo(name, invokerParameterType, checkerParameterType, invoker, checker);

        synchronized (mSynchronizedObject) {
            SparseArray<CommandInfo> sa = mObjectCommands.get(typeCode);
            if (sa != null) {
                // replace instance to make this thread safe...because someone
                // other may have a reference to this map
                sa = cloneSparseArray(sa);
            } else {
                sa = new SparseArray<CommandInfo>();
            }
            sa.put(nameCode, retval);
            mObjectCommands.put(typeCode, sa);
        }

        return retval;
    }

    public static SparseArray<FieldInfo> getAllFields(Class<?> type) {
        int typeCode = type.hashCode();
        SparseArray<FieldInfo> retval = null;

        synchronized (mSynchronizedObject) {
            retval = mObjectFields.get(typeCode);
        }

        if (retval != null) {
            return retval;
        }

        retval = getFieldsForClass(type);

        synchronized (mSynchronizedObject) {
            mObjectFields.put(typeCode, retval);
        }

        return retval;
    }

    public static SparseArray<List<MethodInfo>> getAllMethods(Class<?> type) {
        int typecode = type.hashCode();
        SparseArray<List<MethodInfo>> retval = null;

        synchronized (mSynchronizedObject) {
            retval = mObjectMethods.get(typecode);
        }

        if (retval != null) {
            return retval;
        }

        retval = getMethodsForClass(type);

        synchronized (mSynchronizedObject) {
            mObjectMethods.put(typecode, retval);
        }

        return retval;
    }

    public static List<ConstructorInfo> getAllConstructors(Class<?> type) {
        int typecode = type.hashCode();
        List<ConstructorInfo> retval = null;

        synchronized (mSynchronizedObject) {
            retval = mObjectConstructors.get(typecode);
        }

        if (retval != null) {
            return retval;
        }

        retval = getConstructorsForClass(type);

        synchronized (mSynchronizedObject) {
            mObjectConstructors.put(typecode, retval);
        }

        return retval;
    }

    public static List<MethodInfo> getMethods(Class<?> type, String name) {
        List<MethodInfo> methodList = getAllMethods(type).get(name.hashCode());
        if (methodList == null) {
            methodList = new ArrayList<MethodInfo>();
        }
        return methodList;
    }

    public static MethodInfo getMethod(Class<?> type, final String name,
            final Class<?>... parameterTypes) {
        List<MethodInfo> methods = getAllMethods(type).get(name.hashCode());
        if (methods == null) {
            return null;
        }

        return Iterables
                .monoFrom(methods)
                .filter(matchMethodParameter(parameterTypes))
                .firstOrDefault();
    }

    public static List<ConstructorInfo> getConstructors(Class<?> type) {
        return getAllConstructors(type);
    }

    public static ConstructorInfo getConstructor(Class<?> type, final Class<?>... parameterTypes) {
        List<ConstructorInfo> constructors = getAllConstructors(type);
        if (constructors == null) {
            return null;
        }

        return Iterables
                .monoFrom(constructors)
                .filter(matchConstructorParameter(parameterTypes))
                .firstOrDefault();
    }

    public static FieldInfo getField(Class<?> type, String name) {
        return getAllFields(type).get(name.hashCode());
    }

    public static <T> T createInstance(Class<?> type) throws IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        return createInstance(type, null, null);
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<?> type, Class<?>[] parameterTypes, Object[] parameters)
            throws IllegalArgumentException, InstantiationException, IllegalAccessException,
            InvocationTargetException {
        if (parameterTypes != null
                && (parameters == null || parameters.length != parameterTypes.length)) {
            throw new IllegalArgumentException(
                    "mMethodParameterTypes and parameters must have the same size");
        }

        ConstructorInfo constructor = getConstructor(type, parameterTypes);
        if (constructor == null) {
            throw new InstantiationException("constructor does not exists");
        }

        return (T) constructor.getConstructorOriginal().newInstance(parameters);
    }

    public static boolean canCreateInstance(Class<?> type, Class<?>[] parameterTypes) {
        return getConstructor(type, parameterTypes) != null;
    }

    private static SparseArray<FieldInfo> getFieldsForClass(Class<?> type) {
        Field[] fields = type.getFields();

        SparseArray<FieldInfo> class_fields = new SparseArray<FieldInfo>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            FieldInfo new_field = new FieldInfo(field);
            class_fields.put(new_field.getFieldName().hashCode(), new_field);
        }

        return class_fields;
    }

    private static SparseArray<List<MethodInfo>> getMethodsForClass(Class<?> type) {
        //Method[] ms = type.getDeclaredMethods();
        Method[] typeMethods = type.getMethods();

        SparseArray<List<MethodInfo>> methods = new SparseArray<List<MethodInfo>>(typeMethods.length);
        for (Method method : typeMethods) {
            method.setAccessible(true);
            MethodInfo methodInfo = new MethodInfo(method);
            int methodCode = methodInfo.getMethodName().hashCode();

            List<MethodInfo> methodInfoList = methods.get(methodCode);
            if (methodInfoList == null) {
                methodInfoList = new LinkedList<MethodInfo>();
                methods.put(methodCode, methodInfoList);
            }
            methodInfoList.add(methodInfo);
        }

        return methods;
    }

    private static List<ConstructorInfo> getConstructorsForClass(Class<?> type) {
        Constructor<?>[] cs = type.getDeclaredConstructors();

        List<ConstructorInfo> constructors = new LinkedList<ConstructorInfo>();
        for (Constructor<?> c : cs) {
            constructors.add(new ConstructorInfo(c));
        }

        return constructors;
    }

    private static Func1<MethodInfo, Boolean> matchMethodParameter(final Class<?>[] parameterTypes) {
        return obj -> {
            if (parameterTypes == null) {
                return true;
            }
            if (obj.getMethodParameterCount() != parameterTypes.length) {
                return false;
            }

            for (int i = 0; i < parameterTypes.length; i++) {
                if (!obj.getMethodParameterTypes()[i].equals(parameterTypes[i])) {
                    return false;
                }
            }

            return true;
        };
    }

    private static Func1<ConstructorInfo, Boolean> matchConstructorParameter(final Class<?>[] parameterTypes) {
        return obj -> {
            Class<?>[] pts = parameterTypes;
            if (pts == null) {
                pts = new Class<?>[0];
            }
            if (obj.getConstructorParameterCount() != pts.length) {
                return false;
            }

            for (int i = 0; i < pts.length; i++) {
                if (!obj.getConstructorParameterTypes()[i].equals(pts[i])) {
                    return false;
                }
            }

            return true;
        };
    }

    private static <T> SparseArray<T> cloneSparseArray(SparseArray<T> source) {
        if (source == null) {
            return null;
        }

        SparseArray<T> clone = new SparseArray<T>(source.size());
        for (int i = 0; i < source.size(); i++) {
            clone.put(source.keyAt(i), source.valueAt(i));
        }

        return clone;
    }


}
