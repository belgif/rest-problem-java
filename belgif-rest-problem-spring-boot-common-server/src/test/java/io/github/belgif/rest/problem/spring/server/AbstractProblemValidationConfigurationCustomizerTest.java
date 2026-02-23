package io.github.belgif.rest.problem.spring.server;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import jakarta.validation.Configuration;
import jakarta.validation.ParameterNameProvider;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AbstractProblemValidationConfigurationCustomizerTest {

    private final AbstractProblemValidationConfigurationCustomizer customizer =
            new AbstractProblemValidationConfigurationCustomizer() {
            };

    @Mock
    private Configuration configuration;

    @Captor
    private ArgumentCaptor<ParameterNameProvider> parameterNameProviderCaptor;

    @Test
    void customize() {
        when(configuration.parameterNameProvider(parameterNameProviderCaptor.capture())).thenReturn(configuration);
        customizer.customize(configuration);
        assertThat(parameterNameProviderCaptor.getValue()).isInstanceOf(AnnotationParameterNameProvider.class);
    }

}
