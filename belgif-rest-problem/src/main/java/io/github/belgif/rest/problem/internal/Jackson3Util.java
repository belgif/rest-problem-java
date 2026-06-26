package io.github.belgif.rest.problem.internal;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;

import java.util.ArrayList;
import java.util.List;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import tools.jackson.core.JacksonException.Reference;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;

/**
 * Internal jackson 3 utility class.
 */
public class Jackson3Util {

    private Jackson3Util() {
    }

    /**
     * Convert the given StreamReadException to a BadRequestProblem.
     *
     * @param e the StreamReadException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(StreamReadException e) {
        return new BadRequestProblem(schemaViolation(InEnum.BODY, getName(e.getPath()),
                null, InputValidationIssues.DETAIL_JSON_SYNTAX_ERROR));
    }

    /**
     * Convert the given DatabindException to a BadRequestProblem.
     *
     * @param e the DatabindException
     * @return the BadRequestProblem
     */
    public static BadRequestProblem toBadRequestProblem(DatabindException e) {
        return new BadRequestProblem(InputValidationIssues.schemaViolation(InEnum.BODY, getName(e.getPath()),
                Jackson2Util.getValue(e, e.getOriginalMessage()),
                Jackson2Util.getDetailMessage(e, e.getOriginalMessage())));
    }

    private static String getName(List<Reference> path) {

        if (path.isEmpty()) {
            return null;
        }

        List<String> properties = new ArrayList<>();

        for (Reference reference : path) {
            if (reference.from() instanceof List) {
                // append the index to the property name
                properties.set(properties.size() - 1,
                        properties.get(properties.size() - 1) + "[" + reference.getIndex() + "]");
            } else {
                properties.add(reference.getPropertyName());
            }
        }
        return InputValidationIssue.getNameFromProperties(InEnum.BODY, properties);
    }
}
