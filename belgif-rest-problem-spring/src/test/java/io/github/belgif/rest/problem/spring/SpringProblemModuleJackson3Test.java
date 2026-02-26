package io.github.belgif.rest.problem.spring;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.JacksonModule.SetupContext;
import tools.jackson.databind.jsontype.NamedType;

@ExtendWith(MockitoExtension.class)
class SpringProblemModuleJackson3Test {

    @Mock
    private SetupContext setupContext;

    @Test
    void module() {
        SpringProblemTypeRegistry problemTypeRegistry =
                new SpringProblemTypeRegistry(new ProblemConfigurationProperties());
        SpringProblemModuleJackson3 module = new SpringProblemModuleJackson3(problemTypeRegistry);
        module.setupModule(setupContext);
        verify(setupContext).registerSubtypes(problemTypeRegistry.getProblemTypes().entrySet().stream()
                .map(entry -> new NamedType(entry.getValue(), entry.getKey()))
                .toArray(NamedType[]::new));
    }

}
