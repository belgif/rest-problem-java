package io.github.belgif.rest.problem.quarkus.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;

import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.CdiProblemTypeRegistry;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class ProblemObjectMapperCustomizerTest {

    private final ProblemObjectMapperCustomizer customizer = new ProblemObjectMapperCustomizer();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private CdiProblemTypeRegistry registry;

    @Mock
    private CDI cdi;

    @Mock
    private Instance instance;

    @Test
    void customize() {
        try (MockedStatic<CDI> mock = mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.select(ProblemTypeRegistry.class)).thenReturn(instance);
            when(instance.get()).thenReturn(registry);
            when(registry.getProblemTypes()).thenReturn(new HashMap<>());
            customizer.customize(objectMapper);
            assertThat(objectMapper.getRegisteredModuleIds())
                    .containsExactly("io.github.belgif.rest.problem.ee.CdiProblemModule");
        }
    }

}
