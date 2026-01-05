package io.github.belgif.rest.problem.internal;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.ValueInstantiationException;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;

/**
 * Internal jackson utility class.
 */
public class JacksonUtil {

    private JacksonUtil() {
    }

    /**
     * Convert the given JsonParseException to a BadRequestProblem.
     *
     * @param e the JsonParseException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(JsonParseException e) {
        return new BadRequestProblem(schemaViolation(InEnum.BODY, null,
                null, InputValidationIssues.DETAIL_JSON_SYNTAX_ERROR));
    }

    /**
     * Convert the given JsonMappingException to a BadRequestProblem.
     *
     * @param e the JsonMappingException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(JsonMappingException e) {
        if (e.getCause() instanceof JsonParseException) {
            return toBadRequestProblem((JsonParseException) e.getCause());
        } else {
            return new BadRequestProblem(
                    InputValidationIssues.schemaViolation(InEnum.BODY, getName(e.getPath()), getValue(e),
                            getDetailMessage(e)));
        }
    }

    private static String getName(List<Reference> path) {
        if (path.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Reference reference : path) {
            if (reference.getFrom() instanceof List) {
                builder.append("[").append(reference.getIndex()).append("]");
            } else {
                if (builder.length() > 0) {
                    builder.append(".");
                }
                builder.append(reference.getFieldName());
            }
        }
        return builder.toString();
    }

    private static String getDetailMessage(JsonMappingException e) {
        if (e instanceof InvalidFormatException) {
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

    private static String getValue(JsonMappingException e) {
        Matcher matcher = VALUE_PATTERN.matcher(e.getOriginalMessage());
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

    static final Pattern VALUE_PATTERN = Pattern.compile("from String \"([^\"]+)\"");

    static final Pattern INVALID_FORMAT_PATTERN =
            Pattern.compile("^Cannot deserialize value of type `([^`]+)` from String \"([^\"]+)\": (.+)$");

    static final Pattern VALUE_INSTANTIATION_PATTERN =
            Pattern.compile("^Cannot construct instance of `([^`]+)`, problem: (.+)$");

}
