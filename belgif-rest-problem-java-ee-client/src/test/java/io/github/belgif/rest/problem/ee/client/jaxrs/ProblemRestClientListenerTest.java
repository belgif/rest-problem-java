package io.github.belgif.rest.problem.ee.client.jaxrs;

import static org.mockito.Mockito.*;

import javax.ws.rs.core.Configuration;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.ee.core.util.Platform;

@ExtendWith(MockitoExtension.class)
class ProblemRestClientListenerTest {

    @Mock
    private RestClientBuilder restClientBuilder;

    @Mock
    private Configuration configuration;

    @Test
    void listener() {
        when(restClientBuilder.getConfiguration()).thenReturn(configuration);
        new ProblemRestClientListener().onNewClient(null, restClientBuilder);
        verify(restClientBuilder).register(ProblemResponseExceptionMapper.class);
        verify(restClientBuilder).register(ClientProblemObjectMapperContextResolver.class);
    }

    @Test
    void alreadyRegistered() {
        when(restClientBuilder.getConfiguration()).thenReturn(configuration);
        when(configuration.isRegistered(ProblemResponseExceptionMapper.class)).thenReturn(true);
        when(configuration.isRegistered(ClientProblemObjectMapperContextResolver.class)).thenReturn(true);
        new ProblemRestClientListener().onNewClient(null, restClientBuilder);
        verifyNoMoreInteractions(restClientBuilder);
    }

    @Test
    void quarkus() {
        try (MockedStatic<Platform> mock = mockStatic(Platform.class)) {
            mock.when(Platform::isQuarkus).thenReturn(true);
            when(restClientBuilder.getConfiguration()).thenReturn(configuration);
            new ProblemRestClientListener().onNewClient(null, restClientBuilder);
            verify(restClientBuilder).register(ProblemResponseExceptionMapper.class);
            verifyNoMoreInteractions(restClientBuilder);
        }
    }

}
