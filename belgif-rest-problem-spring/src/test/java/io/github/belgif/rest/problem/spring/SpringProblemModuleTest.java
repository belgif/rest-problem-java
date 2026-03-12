package io.github.belgif.rest.problem.spring;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.Module.SetupContext;
import com.fasterxml.jackson.databind.jsontype.NamedType;

@ExtendWith(MockitoExtension.class)
class SpringProblemModuleTest {

    @Mock
    private SetupContext setupContext;

    @Test
    void module() {
        SpringProblemTypeRegistry problemTypeRegistry =
                new SpringProblemTypeRegistry(new ProblemConfigurationProperties());
        SpringProblemModule module = new SpringProblemModule(problemTypeRegistry);
        module.setupModule(setupContext);
        verify(setupContext).registerSubtypes(problemTypeRegistry.getProblemTypes().entrySet().stream()
                .map(entry -> new NamedType(entry.getValue(), entry.getKey()))
                .toArray(NamedType[]::new));
    }

}
