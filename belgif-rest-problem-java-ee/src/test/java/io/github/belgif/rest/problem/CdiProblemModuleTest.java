package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.registry.CdiProblemTypeRegistry;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class CdiProblemModuleTest {

    @Mock
    private CdiProblemTypeRegistry registry;

    @Mock
    private CDI cdi;

    @Mock
    private Instance instance;

    @Test
    void module() {
        try (MockedStatic<CDI> mock = Mockito.mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.select(ProblemTypeRegistry.class)).thenReturn(instance);
            when(instance.get()).thenReturn(registry);
            when(registry.getProblemTypes()).thenReturn(new NamedType[] {});
            CdiProblemModule module = new CdiProblemModule();
            assertThat(module.getModuleName()).isEqualTo("io.github.belgif.rest.problem.CdiProblemModule");
        }
    }

}
