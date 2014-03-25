
package com.alterego.androidbound.helpers;

import android.util.SparseArray;

import com.alterego.androidbound.helpers.reflector.CommandInfo;
import com.alterego.androidbound.helpers.reflector.ConstructorInfo;
import com.alterego.androidbound.helpers.reflector.FieldInfo;
import com.alterego.androidbound.helpers.reflector.MethodInfo;
import com.alterego.androidbound.helpers.reflector.PropertyInfo;
import com.alterego.androidbound.zzzztoremove.reactive.Iterables;
import com.alterego.androidbound.zzzztoremove.reactive.Predicate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Reflector {

    private static final String COMMAND_PREFIX_DO = "do";
    private static final String COMMAND_PREFIX_CAN = "can";
    private static final String PROPERTY_PREFIX_GET = "get";
    private static final String PROPERTY_PREFIX_SET = "set";
    private static final String METHOD_PREFIX_IS = "is";

    private static SparseArray<SparseArray<PropertyInfo>> mObjectProperties = new SparseArray<SparseArray<PropertyInfo>>();
    private static SparseArray<SparseArray<CommandInfo>> mObjectCommands = new SparseArray<SparseArray<CommandInfo>>();
    private static SparseArray<SparseArray<List<MethodInfo>>> mObjectMethods = new SparseArray<SparseArray<List<MethodInfo>>>();
    private static SparseArray<List<ConstructorInfo>> mObjectConstructors = new SparseArray<List<ConstructorInfo>>();
    private static SparseArray<SparseArray<FieldInfo>> mObjectFields = new SparseArray<SparseArray<FieldInfo>>();
    private static Object mSynchronizedObject = new Object();


    public static boolean isCommand(Class<?> type, String name) {
        List<MethodInfo> invokers = getMethods(type, COMMAND_PREFIX_DO + name);
        if (invokers != null) {
            for (MethodInfo invoker : invokers) {
                if (invoker.getMethodParameterCount() <= 1) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isProperty(Class<?> type, String name) {
        List<MethodInfo> getters = getMethods(type, PROPERTY_PREFIX_GET + name);
        if (getters != null) {
            for (MethodInfo getter : getters) {
                if (getter.getMethodParameterCount() == 0) {
                    return true;
                }
            }
        }

        getters = getMethods(type, METHOD_PREFIX_IS + name);
        if (getters != null) {
            for (MethodInfo mi : getters) {
                if (mi.getMethodParameterCount() == 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public static PropertyInfo getProperty(Class<?> type, String name) {
        int typeCode = type.hashCode();
        int nameCode = name.hashCode();

        PropertyInfo retval = null;
        synchronized (mSynchronizedObject) {
            SparseArray<PropertyInfo> sa = mObjectProperties.get(typeCode);
            if (sa != null) {
                retval = sa.get(nameCode);
            }
        }

        if (retval != null) {
            return retval;
        }

        MethodInfo getter = null;
        MethodInfo setter = null;
        FieldInfo field = null;

        List<MethodInfo> getters = getMethods(type, PROPERTY_PREFIX_GET + name);
        if (getters != null) {
            for (MethodInfo mi : getters) {
                if (mi.getMethodParameterCount() == 0) {
                    getter = mi;
                    break;
                }
            }
        }

        if (getter == null) {
            getters = getMethods(type, METHOD_PREFIX_IS + name);
            if (getters != null) {
                for (MethodInfo mi : getters) {
                    if (mi.getMethodParameterCount() == 0) {
                        getter = mi;
                        break;
                    }
                }
            }
        }

        if (getter != null) {
            List<MethodInfo> setters = getMethods(type, PROPERTY_PREFIX_SET + name);

            if (setters != null) {
                for (MethodInfo mi : setters) {
                    if (mi.getMethodParameterCount() == 1 && getter.getMethodReturnType().equals(mi.getMethodParameterTypes()[0])) {
                        setter = mi;
                        break;
                    }
                }
            }
        }

        if (getter == null) {
            field = Reflector.getField(type, name);
        }

        boolean ismap = Map.class.isAssignableFrom(type);

        retval = new PropertyInfo(name,
                getter != null || field != null || ismap,
                setter != null || field != null || ismap,
                getter != null ? getter.getMethodReturnType() : (field != null ? field.getFieldType() : Object.class),
                getter,
                setter,
                field);

        synchronized (mSynchronizedObject) {
            SparseArray<PropertyInfo> sa = mObjectProperties.get(typeCode);
            if (sa != null) {
                // replace instance to make this thread safe...because someone
                // other may have a reference to this map
                sa = cloneSparseArray(sa);
            } else {
                sa = new SparseArray<PropertyInfo>();
            }
            sa.put(nameCode, retval);
            mObjectProperties.put(typeCode, sa);
        }

        return retval;

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
        return getAllMethods(type).get(name.hashCode());
    }

    public static MethodInfo getMethod(Class<?> type, final String name,
            final Class<?>... parameterTypes) {
        List<MethodInfo> methods = getAllMethods(type).get(name.hashCode());
        if (methods == null) {
            return null;
        }

        return Iterables
                .monoFrom(methods)
                .where(matchMethodParameter(parameterTypes))
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
                .where(matchConstructorParameter(parameterTypes))
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
        //TODO to test
//        Field[] fs = mFieldType.getFields();
//
//        SparseArray<FieldInfo> fields = new SparseArray<Reflector.FieldInfo>(fs.length);
//        for (Field f : fs) {
//            FieldInfo fi = new FieldInfo(f);
//            fields.put(fi.mConstructorName.hashCode(), fi);
//        }

        Field[] fields = type.getDeclaredFields();
        SparseArray<FieldInfo> class_fields = new SparseArray<FieldInfo>(fields.length);
        for (Field field : fields) {
            field.setAccessible(true);
            FieldInfo new_field = new FieldInfo(field);
            class_fields.put(new_field.getFieldName().hashCode(), new_field);
        }

        return class_fields;
    }

    private static SparseArray<List<MethodInfo>> getMethodsForClass(Class<?> type) {
        //TODO to test
        Method[] ms = type.getDeclaredMethods();

        SparseArray<List<MethodInfo>> methods = new SparseArray<List<MethodInfo>>(ms.length);
        for (Method m : ms) {
            m.setAccessible(true);
            MethodInfo mi = new MethodInfo(m);
            int methodCode = mi.getMethodName().hashCode();

            List<MethodInfo> mlist = methods.get(methodCode);
            if (mlist == null) {
                mlist = new LinkedList<MethodInfo>();
                methods.put(methodCode, mlist);
            }
            mlist.add(mi);
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

    private static Predicate<MethodInfo> matchMethodParameter(final Class<?>[] parameterTypes) {
        return new Predicate<MethodInfo>() {
            @Override
            public Boolean invoke(MethodInfo obj) {
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
            }
        };
    }

    private static Predicate<ConstructorInfo> matchConstructorParameter(
            final Class<?>[] parameterTypes) {
        return new Predicate<ConstructorInfo>() {
            @Override
            public Boolean invoke(ConstructorInfo obj) {
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
            }
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
