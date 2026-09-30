package io.github.belgif.rest.problem.it;

/**
 * Problem payload constants.
 */
public class ProblemPayloads {

    public static final String INCONSISTENT_STATUS_CODE = "{\n"
            + "  \"type\": \"urn:problem-type:belgif:badRequest\",\n"
            + "  \"href\": \"https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html\",\n"
            + "  \"title\": \"Bad Request\",\n"
            + "  \"status\": 401,\n" // <- 401 instead of 400
            + "  \"detail\": \"Bad Request with inconsistent problem status code\"\n"
            + "}";

    private ProblemPayloads() {
    }

}
