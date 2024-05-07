package io.github.belgif.rest.problem.jaxrs.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;

import javax.ws.rs.client.Client;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.jaxrs.client.ProblemSupport.ClientInvocationHandler;
import io.github.belgif.rest.problem.jaxrs.client.ProblemSupport.ProxyInvocationHandler;

class ProblemSupportTest {

    interface Service {
    }

    @Test
    void client() {
        Client client = mock(Client.class);
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);
    }

    @Test
    void proxy() {
        Service service = mock(Service.class);
        Service result = ProblemSupport.enable(service);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ProxyInvocationHandler.class);
    }

}
