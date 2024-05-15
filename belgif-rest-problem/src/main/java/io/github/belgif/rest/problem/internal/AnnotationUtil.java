package io.github.belgif.rest.problem.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Optional;

/**
 * Internal annotation utility class.
 */
public class AnnotationUtil {

    private AnnotationUtil() {
    }

    /**
     * Find the first occurence of the given annotation(s) on the parameter with the given name,
     * traversing its super methods (i.e. from superclasses and interfaces) if the annotation is not
     * directly present on the given method itself.
     *
     * <p>
     * In case the given method only has 1 single parameter, that parameter is used as fallback even when
     * it does not match the given parameter name.
     * </p>
     *
     * @param method the method
     * @param paramName the parameter name
     * @param annotations the annotations
     * @return the found annotation
     */
    @SafeVarargs
    public static Optional<Annotation> findParamAnnotation(Method method, String paramName,
            Class<? extends Annotation>... annotations) {
        return findParamAnnotation(method.getDeclaringClass(), method, getParamIndex(method, paramName), annotations);
    }

    /**
     * Find the first occurence of the given annotation(s) on the parameter with the given index,
     * traversing its super methods (i.e. from superclasses and interfaces) if the annotation is not
     * directly present on the given method itself.
     *
     * @param method the method
     * @param paramIndex the parameter index
     * @param annotations the annotations
     * @return the found annotation
     */
    @SafeVarargs
    public static Optional<Annotation> findParamAnnotation(Method method, int paramIndex,
            Class<? extends Annotation>... annotations) {
        return findParamAnnotation(method.getDeclaringClass(), method, paramIndex, annotations);
    }

    @SafeVarargs
    private static Optional<Annotation> findParamAnnotation(Class<?> clazz, Method method, int paramIndex,
            Class<? extends Annotation>... annotations) {
        Method clazzMethod = findMethod(method, clazz);
        if (clazzMethod != null) {
            Parameter parameter = clazzMethod.getParameters()[paramIndex];
            for (Class<? extends Annotation> annotation : annotations) {
                if (parameter.isAnnotationPresent(annotation)) {
                    return Optional.of(parameter.getAnnotation(annotation));
                }
            }
        }
        for (Class<?> intf : clazz.getInterfaces()) {
            Optional<Annotation> intfAnnotation =
                    findParamAnnotation(intf, method, paramIndex, annotations);
            if (intfAnnotation.isPresent()) {
                return intfAnnotation;
            }
        }
        Class<?> superClass = clazz.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            Optional<Annotation> superAnnotation =
                    findParamAnnotation(superClass, method, paramIndex, annotations);
            if (superAnnotation.isPresent()) {
                return superAnnotation;
            }
        }
        return Optional.empty();
    }

    private static int getParamIndex(Method method, String paramName) {
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getName().equals(paramName)) {
                return i;
            }
        }
        if (method.getParameterCount() == 1) {
            return 0;
        }
        throw new IllegalStateException("Could not find parameter " + paramName);
    }

    private static Method findMethod(Method method, Class<?> clazz) {
        if (method.getDeclaringClass().equals(clazz)) {
            return method;
        }
        for (Method clazzMethod : clazz.getDeclaredMethods()) {
            if (clazzMethod.getName().equals(method.getName())
                    && Arrays.equals(clazzMethod.getParameterTypes(), method.getParameterTypes())) {
                return clazzMethod;
            }
        }
        return null;
    }

}
