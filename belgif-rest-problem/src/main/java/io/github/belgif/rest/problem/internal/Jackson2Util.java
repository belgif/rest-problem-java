package io.github.belgif.rest.problem.internal;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Internal jackson 2 utility class.
 */
public class Jackson2Util {

    private static final Pattern VALUE_PATTERN = Pattern.compile("from String \"([^\"]+)\"");

    private static final Pattern INVALID_FORMAT_PATTERN =
            Pattern.compile("^Cannot deserialize value of type `([^`]+)` from String \"([^\"]+)\": (.+)$");

    private static final Pattern VALUE_INSTANTIATION_PATTERN =
            Pattern.compile("^Cannot construct instance of `([^`]+)`, problem: (.+)$");

    private Jackson2Util() {
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
            return new BadRequestProblem(schemaViolation(InEnum.BODY, getName(e.getPath()),
                    null, InputValidationIssues.DETAIL_JSON_SYNTAX_ERROR));
        } else {
            return new BadRequestProblem(
                    InputValidationIssues.schemaViolation(InEnum.BODY, getName(e.getPath()),
                            getValue(e, e.getOriginalMessage()), getDetailMessage(e, e.getOriginalMessage())));
        }
    }

    private static String getName(List<Reference> path) {
        String rootPrefix = ProblemConfig.isJsonPointerEnabled() ? "/" : "";
        String indexPrefix = ProblemConfig.isJsonPointerEnabled() ? "/" : "[";
        String indexSuffix = ProblemConfig.isJsonPointerEnabled() ? "" : "]";
        String fieldNamePrefix = ProblemConfig.isJsonPointerEnabled() ? "/" : ".";

        if (path.isEmpty()) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        for (Reference reference : path) {
            if (reference.getFrom() instanceof List) {
                builder.append(indexPrefix).append(reference.getIndex()).append(indexSuffix);
            } else {
                builder.append(builder.length() > 0 ? fieldNamePrefix : rootPrefix);
                builder.append(reference.getFieldName());
            }
        }
        return builder.toString();
    }

    @SuppressWarnings("java:S1872")
    static String getDetailMessage(Exception e, String originalMessage) {
        if (e.getClass().getSimpleName().equals("InvalidFormatException")) {
            Matcher matcher = INVALID_FORMAT_PATTERN.matcher(originalMessage);
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
                return originalMessage;
            }
        } else if (e.getClass().getSimpleName().equals("ValueInstantiationException")) {
            Matcher matcher = VALUE_INSTANTIATION_PATTERN.matcher(originalMessage);
            if (matcher.matches()) {
                return matcher.group(2);
            } else {
                return originalMessage;
            }
        } else if (originalMessage.startsWith("Missing required")) {
            return "must not be null";
        } else {
            return originalMessage;
        }
    }

    static String getValue(Exception e, String originalMessage) {
        Matcher matcher = VALUE_PATTERN.matcher(originalMessage);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return null;
        }
    }

}
