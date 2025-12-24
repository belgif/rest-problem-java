package io.github.belgif.rest.problem.ee.internal;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import io.github.belgif.rest.problem.api.InEnum;

/**
 * Internal utility class for mapping JAX-RS annotation classes to their corresponding InEnum values.
 *
 * @see InEnum
 */
public class ParameterSourceMapper {

    private static final Map<Class<? extends Annotation>, InEnum> ANNOTATION_MAPPING = new HashMap<>();

    static {
        ANNOTATION_MAPPING.put(QueryParam.class, InEnum.QUERY);
        ANNOTATION_MAPPING.put(PathParam.class, InEnum.PATH);
        ANNOTATION_MAPPING.put(HeaderParam.class, InEnum.HEADER);
        ANNOTATION_MAPPING.put(FormParam.class, InEnum.BODY); // best effort
        ANNOTATION_MAPPING.put(MatrixParam.class, InEnum.PATH); // best effort
        ANNOTATION_MAPPING.put(CookieParam.class, InEnum.HEADER); // best effort
    }

    private static final Map<String, InEnum> NAME_MAPPING = ANNOTATION_MAPPING.entrySet().stream().collect(
            Collectors.toMap(e -> e.getKey().getSimpleName(), Map.Entry::getValue));

    @SuppressWarnings("unchecked")
    private static final Class<? extends Annotation>[] ANNOTATIONS = ANNOTATION_MAPPING.keySet().toArray(new Class[0]);

    private ParameterSourceMapper() {
    }

    public static Class<? extends Annotation>[] getAnnotations() {
        return Arrays.copyOf(ANNOTATIONS, ANNOTATIONS.length);
    }

    public static InEnum map(Class<? extends Annotation> annotation) {
        return ANNOTATION_MAPPING.get(annotation);
    }

    public static InEnum map(String annotationName) {
        return NAME_MAPPING.get(annotationName);
    }

}
