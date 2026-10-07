package io.github.belgif.rest.problem.spring.boot.client;

import io.github.belgif.rest.problem.spring.ProblemConfigurationProperties;
import io.github.belgif.rest.problem.spring.SpringProblemModuleJackson3;
import io.github.belgif.rest.problem.spring.SpringProblemTypeRegistry;
import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson3ErrorHandler;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.json.JsonMapper;

class ProblemResponseJackson3ErrorHandlerTest extends AbstractProblemResponseErrorHandlerTest {

    ProblemResponseJackson3ErrorHandlerTest() {
        super(new ProblemResponseJackson3ErrorHandler(initializeObjectMapper()));
    }

    private static ObjectMapper initializeObjectMapper() {
        return JsonMapper.builder()
                .addModule(new SpringProblemModuleJackson3(
                        new SpringProblemTypeRegistry(new ProblemConfigurationProperties())))
                .build();
    }

}
