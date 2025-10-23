package io.github.belgif.rest.problem.spring;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tools.jackson.databind.JacksonModule;

@ExtendWith(MockitoExtension.class)
class SpringProblemModuleTest {

    @Mock
    private JacksonModule.SetupContext setupContext;

    @Test
    void module() {
        SpringProblemTypeRegistry problemTypeRegistry =
                new SpringProblemTypeRegistry(new ProblemConfigurationProperties());
        SpringProblemModule module = new SpringProblemModule(problemTypeRegistry);
        module.setupModule(setupContext);
        verify(setupContext).registerSubtypes(problemTypeRegistry.getProblemTypes());
    }

}
