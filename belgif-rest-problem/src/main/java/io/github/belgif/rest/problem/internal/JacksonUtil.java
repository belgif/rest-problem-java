package io.github.belgif.rest.problem.internal;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonMappingException.Reference;

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
     * Convert the given JsonMappingException to a BadRequestProblem.
     *
     * @param e the JsonMappingException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(JsonMappingException e) {
        String name = e.getPath().stream().map(JacksonUtil::pathReferenceToString).collect(Collectors.joining("."));
        return new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.BODY, name, null, e.getOriginalMessage()));
    }

    /**
     * Convert the given Reference to a string value to use in a path description.
     *
     * @param reference the Reference from the JsonMappingException path
     * @return the string value
     */
    private static String pathReferenceToString(Reference reference) {
        if (reference.getFrom() != null && reference.getFrom() instanceof List) {
            return "[" + reference.getIndex() + "]";
        } else {
            return reference.getFieldName();
        }
    }

}
