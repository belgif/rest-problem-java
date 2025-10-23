package io.github.belgif.rest.problem.internal;

import java.util.List;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.DatabindException;

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
    public static BadRequestProblem toBadRequestProblem(DatabindException e) {
        StringBuilder name = new StringBuilder();
        for (JacksonException.Reference reference : e.getPath()) {
            if (reference.from() instanceof List) {
                name.append("[").append(reference.getIndex()).append("]");
            } else {
                if (name.length() > 0) {
                    name.append(".");
                }
                name.append(reference.getPropertyName());
            }
        }
        return new BadRequestProblem(
                InputValidationIssues.schemaViolation(InEnum.BODY, name.toString(), null, getDetailMessage(e)));
    }

    private static String getDetailMessage(DatabindException e) {
        if (e.getOriginalMessage().startsWith("Missing required")) {
            return "must not be null";
        } else {
            return e.getOriginalMessage();
        }
    }

}
