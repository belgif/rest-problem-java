package io.github.belgif.rest.problem.ee.jaxrs;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.ext.ParamConverter;
import javax.ws.rs.ext.ParamConverterProvider;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.ee.internal.ParameterSourceMapper;

/**
 * Abstract base class for input-aware ("in" and "name") ParamConverter.
 *
 * <p>
 * Please remember to add the @Provider annotation on classes implementing this interface.
 * </p>
 *
 * @param <T> the parameter type
 */
public abstract class AbstractInputParamConverterProvider<T> implements ParamConverterProvider {

    private final Class<T> type;

    protected AbstractInputParamConverterProvider(Class<T> type) {
        this.type = type;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <E> ParamConverter<E> getConverter(Class<E> rawType, Type genericType, Annotation[] annotations) {
        if (type.isAssignableFrom(rawType)) {
            Input<String> input = new Input<>();
            for (Annotation annotation : annotations) {
                if (ParameterSourceMapper.isParameterAnnotation(annotation.annotationType())) {
                    input.setIn(ParameterSourceMapper.map(annotation.annotationType()));
                    try {
                        input.setName((String) annotation.annotationType().getMethod("value").invoke(annotation));
                    } catch (ReflectiveOperationException e) {
                        throw new IllegalStateException(e);
                    }
                    break;
                }
            }
            return new ParamConverter<E>() {
                @Override
                public E fromString(String value) {
                    return (E) AbstractInputParamConverterProvider.this.fromString(
                            input.getIn(), input.getName(), value);
                }

                @Override
                public String toString(E value) {
                    return AbstractInputParamConverterProvider.this.toString((T) value);
                }
            };
        }
        return null;
    }

    protected abstract T fromString(InEnum in, String name, String value);

    protected String toString(T value) {
        return value == null ? null : value.toString();
    }

}
