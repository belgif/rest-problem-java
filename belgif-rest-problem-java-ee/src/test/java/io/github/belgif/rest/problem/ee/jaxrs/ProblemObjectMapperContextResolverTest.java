package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import javax.annotation.Priority;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.Priorities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.CdiProblemTypeRegistry;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class ProblemObjectMapperContextResolverTest {

    @Mock
    private CdiProblemTypeRegistry registry;

    @Mock
    private CDI cdi;

    @Mock
    private Instance instance;

    @Test
    void mapper() {
        try (MockedStatic<CDI> mock = Mockito.mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.select(ProblemTypeRegistry.class)).thenReturn(instance);
            when(instance.get()).thenReturn(registry);
            when(registry.getProblemTypes()).thenReturn(new HashMap<>());
            ObjectMapper mapper = new ProblemObjectMapperContextResolver().getContext(null);
            assertThat(mapper.getRegisteredModuleIds()).contains("io.github.belgif.rest.problem.ee.CdiProblemModule");
        }
    }

    @Test
    void canBeOverridden() {
        assertThat(ProblemObjectMapperContextResolver.class).hasAnnotation(Priority.class);
        assertThat(ProblemObjectMapperContextResolver.class.getAnnotation(Priority.class).value())
                .isGreaterThan(Priorities.USER);
    }

}
