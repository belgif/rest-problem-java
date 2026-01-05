package io.github.belgif.rest.problem.internal;

import static io.github.belgif.rest.problem.api.InputValidationIssues.invalidStructure;
import static io.github.belgif.rest.problem.internal.JacksonUtil.*;

import java.util.List;
import java.util.regex.Matcher;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import tools.jackson.core.JacksonException;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.core.exc.UnexpectedEndOfInputException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.exc.InvalidFormatException;
import tools.jackson.databind.exc.ValueInstantiationException;

/**
 * Internal jackson utility class.
 */
public class Jackson3Util {

    private Jackson3Util() {
    }

    /**
     * Convert the given JsonMappingException to a BadRequestProblem.
     *
     * @param e the JsonMappingException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(DatabindException e) {
        if (e.getCause() instanceof StreamReadException) {
            return new BadRequestProblem(
                    InputValidationIssues.invalidStructure(InEnum.BODY, getName(e.getPath()), getValue(e),
                            getDetailMessage(e)));
        } else {
            return new BadRequestProblem(
                    InputValidationIssues.schemaViolation(InEnum.BODY, getName(e.getPath()), getValue(e),
                            getDetailMessage(e)));
        }
    }

    private static String getName(List<JacksonException.Reference> path) {
        if (path.isEmpty()) {
            return null;
        }

        StringBuilder name = new StringBuilder();
        for (JacksonException.Reference reference : path) {
            if (reference.from() instanceof List) {
                name.append("[").append(reference.getIndex()).append("]");
            } else {
                if (name.length() > 0) {
                    name.append(".");
                }
                name.append(reference.getPropertyName());
            }
        }
        return name.toString();
    }

    /**
     * Convert the given JsonParseException to a BadRequestProblem.
     *
     * @param e the JsonParseException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(StreamReadException e) {
        return new BadRequestProblem(invalidStructure(InEnum.BODY, getName(e.getPath()),
                null, getDetailMessage(e.getClass())));
    }

    private static String getDetailMessage(Class<? extends StreamReadException> clazz) {
        if (clazz == UnexpectedEndOfInputException.class) {
            return "invalid json data (end-of-input reached unexpectedly)";
        } else {
            return "invalid json data";
        }
    }

    private static String getDetailMessage(DatabindException e) {
        if (e.getCause() != null && e.getCause() instanceof StreamReadException) {
            return getDetailMessage((Class<? extends StreamReadException>) e.getCause().getClass());
        } else if (e instanceof InvalidFormatException) {
            Matcher matcher = INVALID_FORMAT_PATTERN.matcher(e.getOriginalMessage());
            if (matcher.matches()) {
                return matcher.group(3)
                        .replace("accepted for Enum class", "accepted for enumeration")
                        .replace("java.lang.Integer", "int")
                        .replace("java.lang.Short", "int")
                        .replace("java.lang.Long", "int")
                        .replace("java.lang.String", "string")
                        .replace("java.lang.Float", "number")
                        .replace("java.lang.Double", "number");
            } else {
                return e.getOriginalMessage();
            }
        } else if (e instanceof ValueInstantiationException) {
            Matcher matcher = VALUE_INSTANTIATION_PATTERN.matcher(e.getOriginalMessage());
            if (matcher.matches()) {
                return matcher.group(2);
            } else {
                return e.getOriginalMessage();
            }
        } else if (e.getOriginalMessage().startsWith("Missing required")) {
            return "must not be null";
        } else {
            return e.getOriginalMessage();
        }
    }

    private static String getValue(DatabindException e) {
        Matcher matcher = VALUE_PATTERN.matcher(e.getOriginalMessage());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
