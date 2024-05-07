package io.github.belgif.rest.problem;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.registry.CdiProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class CdiProblemModuleTest {

    @Mock
    private CdiProblemTypeRegistry registry;

    @Test
    void module() {
        try (MockedStatic<CdiProblemTypeRegistry> mock = Mockito.mockStatic(CdiProblemTypeRegistry.class)) {
            mock.when(CdiProblemTypeRegistry::instance).thenReturn(registry);
            when(registry.getProblemTypes()).thenReturn(new NamedType[] {});
            CdiProblemModule module = new CdiProblemModule();
            assertThat(new CdiProblemModule().getModuleName())
                    .isEqualTo("io.github.belgif.rest.problem.CdiProblemModule");
        }
    }

}
