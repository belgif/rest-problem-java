package io.github.belgif.rest.problem.ee.resteasy.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;

import javax.ws.rs.core.Configuration;

import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.BadGatewayProblem;
import io.github.belgif.rest.problem.ee.jaxrs.client.ClientProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemClientResponseFilter;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemWrapper;
import io.github.belgif.rest.problem.ee.util.Platform;

@ExtendWith(MockitoExtension.class)
class ResteasyProblemSupportTest {

    @Mock
    private ResteasyWebTarget target;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Configuration configuration;

    @Mock
    private Service serviceMock;

    interface Service {
        String test();
    }

    @BeforeEach
    void mockConfiguration() {
        when(target.getConfiguration()).thenReturn(configuration);
        when(configuration.isRegistered(ProblemClientResponseFilter.class)).thenReturn(true);
        when(configuration.isRegistered(ClientProblemObjectMapperContextResolver.class)).thenReturn(true);
    }

    @Test
    void proxy() {
        when(configuration.isRegistered(ProblemClientResponseFilter.class)).thenReturn(false);
        when(configuration.isRegistered(ClientProblemObjectMapperContextResolver.class)).thenReturn(false);
        when(target.proxy(Service.class)).thenReturn(serviceMock);
        Service service = ResteasyProblemSupport.proxy(target, Service.class);

        assertThat(Proxy.isProxyClass(service.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(service))
                .isInstanceOf(ResteasyProblemSupport.ProxyInvocationHandler.class);

        verify(target).register(ProblemClientResponseFilter.class);
        verify(target).register(ClientProblemObjectMapperContextResolver.class);
    }

    @Test
    void quarkus() {
        when(configuration.isRegistered(ProblemClientResponseFilter.class)).thenReturn(false);
        when(configuration.isRegistered(ClientProblemObjectMapperContextResolver.class)).thenReturn(false);
        when(target.proxy(Service.class)).thenReturn(serviceMock);
        try (MockedStatic<Platform> mock = mockStatic(Platform.class)) {
            mock.when(Platform::isQuarkus).thenReturn(true);
            Service service = ResteasyProblemSupport.proxy(target, Service.class);

            assertThat(Proxy.isProxyClass(service.getClass())).isTrue();
            assertThat(Proxy.getInvocationHandler(service))
                    .isInstanceOf(ResteasyProblemSupport.ProxyInvocationHandler.class);

            verify(target).register(ProblemClientResponseFilter.class);
            verifyNoMoreInteractions(target);
        }
    }

    @Test
    void normalResponseFromProxyClient() {
        when(target.proxy(Service.class)).thenReturn(serviceMock);
        Service service = ResteasyProblemSupport.proxy(target, Service.class);

        when(serviceMock.test()).thenReturn("OK");

        assertThat(service.test()).isEqualTo("OK");
    }

    @Test
    void unwrapProblemWrapperInProxyClient() {
        when(target.proxy(Service.class)).thenReturn(serviceMock);
        Service service = ResteasyProblemSupport.proxy(target, Service.class);

        doThrow(new ProblemWrapper(new BadGatewayProblem())).when(serviceMock).test();

        assertThatExceptionOfType(BadGatewayProblem.class).isThrownBy(service::test);
    }

    @Test
    void otherExceptionInProxyClient() {
        when(target.proxy(Service.class)).thenReturn(serviceMock);
        Service service = ResteasyProblemSupport.proxy(target, Service.class);

        doThrow(new RuntimeException("other")).when(serviceMock).test();

        assertThatRuntimeException().isThrownBy(service::test).withMessage("other");
    }

}
