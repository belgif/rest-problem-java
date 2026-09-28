package io.github.belgif.rest.problem.internal;

import static io.github.belgif.rest.problem.api.InputValidationIssues.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssues;
import io.github.belgif.rest.problem.api.Problem;
import tools.jackson.core.JacksonException.Reference;
import tools.jackson.core.exc.StreamReadException;
import tools.jackson.databind.DatabindException;
import tools.jackson.databind.JsonNode;

/**
 * Internal jackson 3 utility class.
 */
public class Jackson3Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Jackson3Util.class);

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

    /**
     * Check whether the given JsonNode is a Belgif-compliant /health response with status DOWN.
     *
     * @param payload the response payload
     * @return true when health DOWN response, false otherwise
     */
    public static boolean isHealthDownResponse(JsonNode payload) {
        return payload.has("status") && payload.get("status").isString()
                && "DOWN".equals(payload.get("status").asString());
    }

    /**
     * Check consistency between HTTP status code, status code in JSON payload, and mapped Problem status code.
     *
     * @param httpStatusCode the HTTP status code
     * @param payload the response payload
     * @param problem the mapped Problem
     * @return true when the provided status codes are consistent, false when inconsistent
     */
    public static boolean checkStatusCodeConsistency(Integer httpStatusCode, JsonNode payload, Problem problem) {
        Integer payloadStatusCode =
                (payload.has("status") && payload.get("status").isInt()) ? payload.get("status").asInt() : null;
        Integer problemStatusCode = problem.getStatus();
        if (Stream.of(httpStatusCode, payloadStatusCode, problemStatusCode).collect(Collectors.toSet()).size() > 1) {
            LOGGER.warn("Detected inconsistency in problem status code: HTTP={}, JSON={}, Problem={}",
                    httpStatusCode, payloadStatusCode, problemStatusCode);
            return false;
        }
        return true;
    }

    private static String getName(List<Reference> path) {
        if (path.isEmpty()) {
            return null;
        }
        StringBuilder name = new StringBuilder();
        for (Reference reference : path) {
            if (reference.from() instanceof List) {
                name.append("[").append(reference.getIndex()).append("]");
            } else {
                if (name.length() > 0) {
                    name.append(".");
                }
                name.append(reference.getPropertyName());
            }
        }
        return JsonPointerUtil.transformName(InEnum.BODY, name.toString());
    }
}
