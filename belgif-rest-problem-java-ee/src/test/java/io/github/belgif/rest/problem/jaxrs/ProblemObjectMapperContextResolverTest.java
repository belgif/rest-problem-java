package io.github.belgif.rest.problem.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.registry.CdiProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class ProblemObjectMapperContextResolverTest {

    @Mock
    private CdiProblemTypeRegistry registry;

    @Test
    void mapper() {
        try (MockedStatic<CdiProblemTypeRegistry> mock = Mockito.mockStatic(CdiProblemTypeRegistry.class)) {
            mock.when(CdiProblemTypeRegistry::instance).thenReturn(registry);
            when(registry.getProblemTypes()).thenReturn(new NamedType[] {});
            ObjectMapper mapper = new ProblemObjectMapperContextResolver().getContext(null);
            assertThat(mapper.getRegisteredModuleIds()).contains("io.github.belgif.rest.problem.CdiProblemModule");
            assertThat(ProblemObjectMapperContextResolver.getObjectMapper()).isSameAs(mapper);
        }
    }

    @Test
    void canBeOverridden() {
        assertThat(ProblemObjectMapperContextResolver.class).hasAnnotation(Priority.class);
        assertThat(ProblemObjectMapperContextResolver.class.getAnnotation(Priority.class).value())
                .isGreaterThan(Priorities.USER);
    }

}
