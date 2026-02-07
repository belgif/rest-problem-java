package io.github.belgif.rest.problem.ee;

import static org.mockito.Mockito.*;

import java.util.HashMap;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

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
            when(registry.getProblemTypes()).thenReturn(new HashMap<>());
            CdiProblemModule module = new CdiProblemModule();
            Assertions.assertThat(module.getModuleName())
                    .isEqualTo("io.github.belgif.rest.problem.ee.CdiProblemModule");
        }
    }

}
