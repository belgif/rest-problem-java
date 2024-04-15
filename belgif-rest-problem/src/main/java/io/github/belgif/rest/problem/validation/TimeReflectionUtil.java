package io.github.belgif.rest.problem.validation;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class TimeReflectionUtil {

    private static final Map<Class<?>, Field> START_DATE_FIELDS = new ConcurrentHashMap<>();
    private static final Map<Class<?>, Field> END_DATE_FIELDS = new ConcurrentHashMap<>();

    private static final Class<LocalDate> SUPPORTED_DATE_TYPE = LocalDate.class;
    private static final String START_FIELD_NAME = "startDate";
    private static final String END_FIELD_NAME = "endDate";

    private TimeReflectionUtil() {
    }

    private static LocalDate getDateValue(Object o, Field field) {
        try {
            return (LocalDate) field.get(o);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the value of the start date field in an object
     *
     * @param o Object featuring a start date
     * @return the start local date
     * @throws IllegalArgumentException when no start date of the supported date type is found in the object
     */
    public static LocalDate getStartDate(Object o) {
        Field startField =
                START_DATE_FIELDS.computeIfAbsent(o.getClass(), key -> findField(o.getClass(), START_FIELD_NAME));
        return getDateValue(o, startField);
    }

    /**
     * Get the value of the end date field in an object
     *
     * @param o Object featuring an end date
     * @return the end local date
     * @throws IllegalArgumentException when no end date of the supported date type is found in the object
     */
    public static LocalDate getEndDate(Object o) {
        Field endField =
                END_DATE_FIELDS.computeIfAbsent(o.getClass(), key -> findField(o.getClass(), END_FIELD_NAME));
        return getDateValue(o, endField);
    }

    private static Field findField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (fieldName.equals(field.getName()) && SUPPORTED_DATE_TYPE.equals(field.getType())) {
                field.setAccessible(true);
                return field;
            }
        }
        throw new IllegalArgumentException(
                "No " + fieldName + " field with type " + SUPPORTED_DATE_TYPE + " was found on " + clazz);
    }
}
