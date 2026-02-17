package io.github.belgif.rest.problem.ee.jaxrs.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.ws.rs.client.AsyncInvoker;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.BadGatewayProblem;
import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemSupport.ClientInvocationHandler;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemSupport.ProxyInvocationHandler;

@ExtendWith(MockitoExtension.class)
class ProblemSupportTest {

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Client client;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private Configuration configuration;

    interface Service {
        String test();
    }

    @BeforeEach
    public void mockConfiguration() {
        when(client.getConfiguration()).thenReturn(configuration);
        when(configuration.isRegistered(ProblemClientResponseFilter.class)).thenReturn(true);
    }

    @Test
    void registerProblemClientResponseFilter() {
        reset(configuration);
        when(configuration.isRegistered(ProblemClientResponseFilter.class)).thenReturn(false);
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        Invocation invocation = mock(Invocation.class);
        when(builder.buildGet()).thenReturn(invocation);

        when(invocation.invoke()).thenThrow(new ProblemWrapper(new BadRequestProblem()));

        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> result.target("https://www.belgif.be").request().buildGet().invoke());

        verify(client).register(ProblemClientResponseFilter.class);
    }

    @Test
    void unwrapProblemWrapperInJaxRsClient() {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        Invocation invocation = mock(Invocation.class);
        when(builder.buildGet()).thenReturn(invocation);

        when(invocation.invoke()).thenThrow(new ProblemWrapper(new BadRequestProblem()));

        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> result.target("https://www.belgif.be").request().buildGet().invoke());
    }

    @Test
    void normalResponseFromJaxRsClient() {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        Invocation invocation = mock(Invocation.class);
        when(builder.buildGet()).thenReturn(invocation);

        Response response = Response.ok().build();
        when(invocation.invoke()).thenReturn(response);

        assertThat(result.target("https://www.belgif.be").request().buildGet().invoke()).isEqualTo(response);
    }

    @Test
    void unwrapProblemWrapperInAsyncJaxRsClient() throws Exception {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        AsyncInvoker asyncInvoker = mock(AsyncInvoker.class);
        when(builder.async()).thenReturn(asyncInvoker);

        Future<Response> future = mock(Future.class);
        when(asyncInvoker.get()).thenReturn(future);

        when(future.get()).thenThrow(new ExecutionException(new ProblemWrapper(new BadRequestProblem())));

        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> result.target("https://www.belgif.be").request().async().get().get());
    }

    @Test
    void normalResponseFromAsyncJaxRsClient() throws Exception {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        AsyncInvoker asyncInvoker = mock(AsyncInvoker.class);
        when(builder.async()).thenReturn(asyncInvoker);

        Future<Response> future = mock(Future.class);
        when(asyncInvoker.get()).thenReturn(future);

        Response response = Response.ok().build();
        when(future.get()).thenReturn(response);

        assertThat(result.target("https://www.belgif.be").request().async().get().get()).isEqualTo(response);
    }

    @Test
    void otherExceptionInAsyncJaxRsClient() throws Exception {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        AsyncInvoker asyncInvoker = mock(AsyncInvoker.class);
        when(builder.async()).thenReturn(asyncInvoker);

        Future<Response> future = mock(Future.class);
        when(asyncInvoker.get()).thenReturn(future);

        when(future.get()).thenThrow(new ExecutionException(new RuntimeException("other")));

        assertThatExceptionOfType(ExecutionException.class)
                .isThrownBy(() -> result.target("https://www.belgif.be").request().async().get().get())
                .withRootCauseInstanceOf(RuntimeException.class)
                .withMessageContaining("other");
    }

    @Test
    void otherExceptionInJaxRsClient() {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        WebTarget target = mock(WebTarget.class);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        when(builder.get()).thenThrow(new RuntimeException("other"));

        assertThatRuntimeException()
                .isThrownBy(() -> result.target("https://www.belgif.be").request().get())
                .withMessage("other");
    }

    @Test
    void exceptionCreatingProxiedReturnType() {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        when(client.target("https://www.belgif.be")).thenThrow(new RuntimeException("oops"));

        assertThatRuntimeException()
                .isThrownBy(() -> result.target("https://www.belgif.be"))
                .withMessage("oops");
    }

    @Test
    void unwrapProblemWrapperInProxyClient() {
        Service service = mock(Service.class);
        Service result = ProblemSupport.enable(service);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ProxyInvocationHandler.class);

        doThrow(new ProblemWrapper(new BadGatewayProblem())).when(service).test();

        assertThatExceptionOfType(BadGatewayProblem.class).isThrownBy(result::test);
    }

    @Test
    void normalResponseFromProxyClient() {
        Service service = mock(Service.class);
        Service result = ProblemSupport.enable(service);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ProxyInvocationHandler.class);

        when(service.test()).thenReturn("OK");

        assertThat(result.test()).isEqualTo("OK");
    }

    @Test
    void otherExceptionInProxyClient() {
        Service service = mock(Service.class);
        Service result = ProblemSupport.enable(service);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ProxyInvocationHandler.class);

        doThrow(new RuntimeException("other")).when(service).test();

        assertThatRuntimeException().isThrownBy(result::test).withMessage("other");
    }

    @Test
    void configurable() {
        Client result = ProblemSupport.enable(client);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);

        when(client.register("test")).thenReturn(client);

        WebTarget target = mock(WebTarget.class);

        when(target.register("test")).thenReturn(target);
        when(client.target("https://www.belgif.be")).thenReturn(target);

        Invocation.Builder builder = mock(Invocation.Builder.class);
        when(target.request()).thenReturn(builder);

        Invocation invocation = mock(Invocation.class);
        when(builder.buildGet()).thenReturn(invocation);

        when(invocation.invoke()).thenThrow(new ProblemWrapper(new BadRequestProblem()));

        assertThatExceptionOfType(BadRequestProblem.class)
                .isThrownBy(() -> result
                        .register("test")
                        .target("https://www.belgif.be")
                        .register("test")
                        .request().buildGet().invoke());
    }

}
