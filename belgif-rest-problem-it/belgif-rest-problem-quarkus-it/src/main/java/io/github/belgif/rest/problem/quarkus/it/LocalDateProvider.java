package io.github.belgif.rest.problem.quarkus.it;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.time.LocalDate;

import jakarta.ws.rs.ext.ParamConverter;
import jakarta.ws.rs.ext.ParamConverterProvider;
import jakarta.ws.rs.ext.Provider;

@Provider
public class LocalDateProvider implements ParamConverterProvider {

    private static final LocalDateConverter CONVERTER = new LocalDateConverter();

    @SuppressWarnings("unchecked")
    @Override
    public <T> ParamConverter<T> getConverter(Class<T> rawType, Type genericType, Annotation[] annotations) {
        if (rawType == LocalDate.class) {
            return (ParamConverter<T>) CONVERTER;
        }
        return null;
    }

}
