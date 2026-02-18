package io.github.belgif.rest.problem.ee.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.CdiProblemModule;

/**
 * ObjectMapper that registers the CdiProblemModule.
 */
public class ProblemObjectMapper extends ObjectMapper {

    public static final ProblemObjectMapper INSTANCE = new ProblemObjectMapper();

    private ProblemObjectMapper() {
        registerModule(new CdiProblemModule());
    }

}
