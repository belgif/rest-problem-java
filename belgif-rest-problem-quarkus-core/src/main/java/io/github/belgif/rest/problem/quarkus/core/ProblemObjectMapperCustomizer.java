package io.github.belgif.rest.problem.quarkus.core;

import jakarta.inject.Singleton;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.CdiProblemModule;
import io.quarkus.jackson.ObjectMapperCustomizer;

/**
 * ObjectMapperCustomizer that registers the CdiProblemModule.
 */
@Singleton
public class ProblemObjectMapperCustomizer implements ObjectMapperCustomizer {

    @Override
    public void customize(ObjectMapper objectMapper) {
        objectMapper.registerModule(new CdiProblemModule());
    }

}
