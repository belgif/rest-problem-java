package io.github.belgif.rest.problem.registry;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.ProcessAnnotatedType;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.api.ProblemType;

@ExtendWith(MockitoExtension.class)
class CdiProblemTypeRegistryTest {

    private CdiProblemTypeRegistry registry = new CdiProblemTypeRegistry();

    @Mock
    private ProcessAnnotatedType processAnnotatedType;

    @Mock
    private AnnotatedType annotatedType;

    @Test
    void processAnnotatedTypeSuccess() {
        when(processAnnotatedType.getAnnotatedType()).thenReturn(annotatedType);
        when(annotatedType.getAnnotation(ProblemType.class))
                .thenReturn(BadRequestProblem.class.getAnnotation(ProblemType.class));
        when(annotatedType.getJavaClass()).thenReturn(BadRequestProblem.class);

        registry.processAnnotatedType(processAnnotatedType);

        verify(processAnnotatedType).veto();

        registry.afterTypeDiscovery(null);

        assertThat(registry.getProblemTypes()).hasSize(1);
        NamedType registeredType = registry.getProblemTypes()[0];

        assertThat(registeredType.getType()).isEqualTo(BadRequestProblem.class);
        assertThat(registeredType.getName()).isEqualTo("urn:problem-type:belgif:badRequest");
    }

    @Test
    void processAnnotatedTypeNoAnnotation() {
        when(processAnnotatedType.getAnnotatedType()).thenReturn(annotatedType);
        when(annotatedType.getAnnotation(ProblemType.class)).thenReturn(null);
        when(annotatedType.getJavaClass()).thenReturn(DefaultProblem.class);

        registry.processAnnotatedType(processAnnotatedType);

        verify(processAnnotatedType).veto();

        registry.afterTypeDiscovery(null);

        assertThat(registry.getProblemTypes()).isEmpty();
    }

}
