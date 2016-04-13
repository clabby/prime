package me.vexil.prime.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReflectionUtil {

    private static Map<String, Class<?>> loadedClasses = new HashMap<>();
    private static Map<Class<?>, Map<String, Map<Class<?>[], Method>>> loadedMethods = new HashMap<>();
    private static Map<Class<?>, Map<String, Field>> loadedFields = new HashMap<>();

    public static final String MC_VERSION;
    public static final String NMS;
    public static final String OBC;

    static {
        String version = null;

        StartLoop:
        for (int a = 0; a < 10; a++) {
            for (int b = 0; b < 10; b++) {
                for (int c = 0; c < 10; c++) {
                    version = "v" + a + "_" + b + "_R" + c;
                    if (versionExists(version)) {
                        break StartLoop;
                    }
                }
            }
        }

        MC_VERSION = version;
        NMS = "net.minecraft.server." + MC_VERSION + ".";
        OBC = "org.bukkit.craftbukkit." + MC_VERSION + ".";
    }

    private static boolean versionExists(String version) {
        try {
            Class.forName("net.minecraft.server." + version + ".Packet");
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Class<?> getClass(String className) {
        if (loadedClasses.containsKey(className)) {
            return loadedClasses.get(className);
        }
        try {
            Class clazz = Class.forName(className);
            if (clazz != null) {
                loadedClasses.put(className, clazz);
            }
            return clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getNMSClass(String className) {
        String fullName = NMS + className;
        return getClass(fullName);
    }

    public static Class<?> getOBCClass(String className) {
        String fullName = OBC + className;
        return getClass(fullName);
    }

    public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        Class<?>[] params = parameters == null ? new Class<?>[0] : parameters;
        Map<String, Map<Class<?>[], Method>> loadedMethodNames = getLoadedMethods(clazz);
        Map<Class<?>[], Method> loadedMethodParams = getLoadedMethods(clazz, methodName);

        Method method = getLoadedMethod(clazz, methodName, params);
        if (method == null) {
            try {
                method = clazz.getDeclaredMethod(methodName, parameters);
                method.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        loadedMethodParams.put(params, method);
        loadedMethodNames.put(methodName, loadedMethodParams);
        loadedMethods.put(clazz, loadedMethodNames);
        return method;
    }

    public static Map<String, Map<Class<?>[], Method>> getLoadedMethods(Class<?> clazz) {
        Map<String, Map<Class<?>[], Method>> loadedMethods = ReflectionUtil.loadedMethods.get(clazz);
        if (loadedMethods == null) {
            loadedMethods = new HashMap<>();
        }
        return loadedMethods;
    }

    public static Map<Class<?>[], Method> getLoadedMethods(Class<?> clazz, String methodName) {
        Map<Class<?>[], Method> loadedMethods = getLoadedMethods(clazz).get(methodName);
        if (loadedMethods == null) {
            loadedMethods = new HashMap<>();
        }
        return loadedMethods;
    }

    public static Method getLoadedMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
        return getLoadedMethods(clazz, methodName).get(parameters);
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        Map<String, Field> loadedFields = getLoadedFields(clazz);
        if (loadedFields.containsKey(fieldName)) {
            return loadedFields.get(fieldName);
        }
        Field field = null;
        try {
            field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        loadedFields.put(fieldName, field);
        ReflectionUtil.loadedFields.put(clazz, loadedFields);
        return field;
    }

    public static Method getMethod(String className, String methodName, Class<?>... params) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = clazz.getDeclaredMethod(methodName, params);
            method.setAccessible(true);
            return method;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Field getField(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Field field = clazz.getDeclaredField(methodName);
            field.setAccessible(true);
            return field;
        } catch (ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Class<?> classFrom(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Object getFieldValue(Class<?> clazz, Object instance, String fieldName) {
        return getFieldValue(getField(clazz, fieldName), instance);
    }

    public static Object getFieldValue(Object instance, String fieldName) {
        return getFieldValue(getField(instance.getClass(), fieldName), instance);
    }

    public static Object getFieldValue(Field field, Object instance) {
        try {
            return field.get(instance);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Map<String, Field> getLoadedFields(Class<?> clazz) {
        Map<String, Field> loadedFields = ReflectionUtil.loadedFields.get(clazz);
        if (loadedFields == null) {
            loadedFields = new HashMap<>();
        }
        return loadedFields;
    }

    public static void setFieldValue(Object instance, String fieldName, Object value) {
        try {
            getField(instance.getClass(), fieldName).set(instance, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static Object invoke(Method method, Object instance, Object... parameters) {
        if (method == null) {
            return null;
        }
        try {
            return method.invoke(instance, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Object invokeStatic(Method method, Object... parameters) {
        return invoke(method, null, parameters);
    }

    public static <T> Constructor<T> getConstructor(Class<T> clazz, Class<?>... parameters) {
        try {
            return clazz.getConstructor(parameters);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T newInstance(Constructor<T> constructor, Object... parameters) {
        try {
            return constructor.newInstance(parameters);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Class[] convert(Object[] parameters) {
        Class<?>[] classParams = null;
        if (parameters != null) {
            if (parameters.length <= 0) {
                classParams = new Class<?>[0];
            } else {
                List<Class<?>> params = new ArrayList<>();
                for (Object param : parameters) {
                    params.add(param.getClass());
                    classParams = params.toArray(new Class[0]);
                }
            }
        }
        return classParams;
    }
}