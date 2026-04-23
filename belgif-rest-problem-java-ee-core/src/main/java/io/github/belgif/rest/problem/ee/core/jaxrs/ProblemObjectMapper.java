package io.github.belgif.rest.problem.ee.core.jaxrs;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.core.CdiProblemModule;

/**
 * ObjectMapper that registers the CdiProblemModule.
 */
public class ProblemObjectMapper extends ObjectMapper {

    public static final ProblemObjectMapper INSTANCE = new ProblemObjectMapper();

    private ProblemObjectMapper() {
        registerModule(new CdiProblemModule());
    }

}
