package io.github.belgif.rest.problem.ee.jaxrs.client;

import static org.mockito.Mockito.*;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener.ClientProblemObjectMapperContextResolver;

@ExtendWith(MockitoExtension.class)
class ProblemRestClientListenerTest {

    @Mock
    private RestClientBuilder restClientBuilder;

    @Test
    void listener() {
        new ProblemRestClientListener().onNewClient(null, restClientBuilder);
        verify(restClientBuilder).register(ClientProblemObjectMapperContextResolver.class);
        verify(restClientBuilder).register(ProblemResponseExceptionMapper.class);
    }

}
