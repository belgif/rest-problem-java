package io.github.belgif.rest.problem.spring.boot.client;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.spring.ProblemConfigurationProperties;
import io.github.belgif.rest.problem.spring.SpringProblemModule;
import io.github.belgif.rest.problem.spring.SpringProblemTypeRegistry;
import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson2ErrorHandler;

class ProblemResponseJackson2ErrorHandlerTest extends AbstractProblemResponseErrorHandlerTest {

    ProblemResponseJackson2ErrorHandlerTest() {
        super(new ProblemResponseJackson2ErrorHandler(initializeObjectMapper()));
    }

    private static ObjectMapper initializeObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(
                new SpringProblemModule(new SpringProblemTypeRegistry(new ProblemConfigurationProperties())));
        return mapper;
    }

}
