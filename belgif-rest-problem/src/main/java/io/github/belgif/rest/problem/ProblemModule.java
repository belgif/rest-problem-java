package io.github.belgif.rest.problem;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * Problem module that can be registered on jackson {@link com.fasterxml.jackson.databind.ObjectMapper}.
 *
 * @see com.fasterxml.jackson.databind.ObjectMapper#registerModule(Module)
 */
public class ProblemModule extends SimpleModule {

    public ProblemModule(ProblemTypeRegistry problemTypeRegistry) {
        registerSubtypes(problemTypeRegistry.getProblemTypes());
    }

}
