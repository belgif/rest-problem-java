package io.github.belgif.rest.problem.internal;

import java.util.List;

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
        StringBuilder name = new StringBuilder();
        for (Reference reference : e.getPath()) {
            if (reference.getFrom() instanceof List) {
                name.append("[").append(reference.getIndex()).append("]");
            } else {
                if (name.length() > 0) {
                    name.append(".");
                }
                name.append(reference.getFieldName());
            }
        }
        return new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.BODY, name.toString(), null, getDetailMessage(e)));
    }

    private static String getDetailMessage(JsonMappingException e) {
        if (e.getOriginalMessage().startsWith("Missing required")) {
            return "must not be null";
        } else {
            return e.getOriginalMessage();
        }
    }

}
