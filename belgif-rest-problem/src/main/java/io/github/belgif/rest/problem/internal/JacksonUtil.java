package io.github.belgif.rest.problem.internal;

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
        String name = e.getPath().stream().map(Reference::getFieldName).collect(Collectors.joining("."));
        return new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.BODY, name, null, e.getOriginalMessage()));
    }

}
