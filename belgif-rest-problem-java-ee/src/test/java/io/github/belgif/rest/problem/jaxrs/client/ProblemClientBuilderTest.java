package io.github.belgif.rest.problem.jaxrs.client;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.lang.reflect.Proxy;
import java.security.KeyStore;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.core.Configuration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.jaxrs.client.ProblemSupport.ClientInvocationHandler;

@ExtendWith(MockitoExtension.class)
class ProblemClientBuilderTest {

    @Mock
    private ProblemClientBuilder delegate;

    @InjectMocks
    private ProblemClientBuilder builder;

    @Test
    void build() {
        Client client = mock(Client.class);
        when(delegate.build()).thenReturn(client);
        Client result = builder.build();
        assertThat(result).isInstanceOf(Client.class);
        assertThat(Proxy.isProxyClass(result.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(result)).isInstanceOf(ClientInvocationHandler.class);
    }

    @Test
    void delegateGetConfiguration() {
        Configuration config = mock(Configuration.class);
        when(delegate.getConfiguration()).thenReturn(config);
        assertThat(builder.getConfiguration()).isEqualTo(config);
    }

    @Test
    void delegateWithConfig() {
        Configuration config = mock(Configuration.class);
        when(delegate.withConfig(config)).thenReturn(delegate);
        assertThat(builder.withConfig(config)).isSameAs(builder);
        verify(delegate).withConfig(config);
    }

    @Test
    void delegateSslContext() {
        SSLContext context = mock(SSLContext.class);
        when(delegate.sslContext(context)).thenReturn(delegate);
        assertThat(builder.sslContext(context)).isSameAs(builder);
        verify(delegate).sslContext(context);
    }

    @Test
    void delegateKeyStoreWithCharArray() {
        KeyStore keyStore = mock(KeyStore.class);
        char[] password = "password".toCharArray();
        when(delegate.keyStore(keyStore, password)).thenReturn(delegate);
        assertThat(builder.keyStore(keyStore, password)).isSameAs(builder);
        verify(delegate).keyStore(keyStore, password);
    }

    @Test
    void delegateKeyStoreWithString() {
        KeyStore keyStore = mock(KeyStore.class);
        String password = "password";
        when(delegate.keyStore(keyStore, password)).thenReturn(delegate);
        assertThat(builder.keyStore(keyStore, password)).isSameAs(builder);
        verify(delegate).keyStore(keyStore, password);
    }

    @Test
    void delegateTrustStore() {
        KeyStore keyStore = mock(KeyStore.class);
        when(delegate.trustStore(keyStore)).thenReturn(delegate);
        assertThat(builder.trustStore(keyStore)).isSameAs(builder);
        verify(delegate).trustStore(keyStore);
    }

    @Test
    void delegateHostnameVerifier() {
        HostnameVerifier verifier = mock(HostnameVerifier.class);
        when(delegate.hostnameVerifier(verifier)).thenReturn(delegate);
        assertThat(builder.hostnameVerifier(verifier)).isSameAs(builder);
        verify(delegate).hostnameVerifier(verifier);
    }

    @Test
    void delegateExecutorService() {
        ExecutorService executorService = mock(ExecutorService.class);
        when(delegate.executorService(executorService)).thenReturn(delegate);
        assertThat(builder.executorService(executorService)).isSameAs(builder);
        verify(delegate).executorService(executorService);
    }

    @Test
    void delegateScheduledExecutorService() {
        ScheduledExecutorService scheduledExecutorService = mock(ScheduledExecutorService.class);
        when(delegate.scheduledExecutorService(scheduledExecutorService)).thenReturn(delegate);
        assertThat(builder.scheduledExecutorService(scheduledExecutorService)).isSameAs(builder);
        verify(delegate).scheduledExecutorService(scheduledExecutorService);
    }

    @Test
    void delegateConnectTimeout() {
        long timeout = 10L;
        TimeUnit unit = TimeUnit.SECONDS;
        when(delegate.connectTimeout(timeout, unit)).thenReturn(delegate);
        assertThat(builder.connectTimeout(timeout, unit)).isSameAs(builder);
        verify(delegate).connectTimeout(timeout, unit);
    }

    @Test
    void delegateReadTimeout() {
        long timeout = 10L;
        TimeUnit unit = TimeUnit.SECONDS;
        when(delegate.readTimeout(timeout, unit)).thenReturn(delegate);
        assertThat(builder.readTimeout(timeout, unit)).isSameAs(builder);
        verify(delegate).readTimeout(timeout, unit);
    }

    @Test
    void delegateProperty() {
        String name = "propertyName";
        Object value = new Object();
        when(delegate.property(name, value)).thenReturn(delegate);
        assertThat(builder.property(name, value)).isSameAs(builder);
        verify(delegate).property(name, value);
    }

    @Test
    void delegateRegisterClass() {
        Class<?> componentClass = Object.class;
        when(delegate.register(componentClass)).thenReturn(delegate);
        assertThat(builder.register(componentClass)).isSameAs(builder);
        verify(delegate).register(componentClass);
    }

    @Test
    void delegateRegisterClassWithPriority() {
        Class<?> componentClass = Object.class;
        int priority = 1;
        when(delegate.register(componentClass, priority)).thenReturn(delegate);
        assertThat(builder.register(componentClass, priority)).isSameAs(builder);
        verify(delegate).register(componentClass, priority);
    }

    @Test
    void delegateRegisterClassWithContracts() {
        Class<?> componentClass = Object.class;
        Class<?>[] contracts = { Comparable.class };
        when(delegate.register(componentClass, contracts)).thenReturn(delegate);
        assertThat(builder.register(componentClass, contracts)).isSameAs(builder);
        verify(delegate).register(componentClass, contracts);
    }

    @Test
    void delegateRegisterClassWithMapOfContracts() {
        Class<?> componentClass = Object.class;
        Map<Class<?>, Integer> contracts = new HashMap<>();
        when(delegate.register(componentClass, contracts)).thenReturn(delegate);
        assertThat(builder.register(componentClass, contracts)).isSameAs(builder);
        verify(delegate).register(componentClass, contracts);
    }

    @Test
    void delegateRegisterObject() {
        Object component = new Object();
        when(delegate.register(component)).thenReturn(delegate);
        assertThat(builder.register(component)).isSameAs(builder);
        verify(delegate).register(component);
    }

    @Test
    void delegateRegisterObjectWithPriority() {
        Object component = new Object();
        int priority = 1;
        when(delegate.register(component, priority)).thenReturn(delegate);
        assertThat(builder.register(component, priority)).isSameAs(builder);
        verify(delegate).register(component, priority);
    }

    @Test
    void delegateRegisterObjectWithContracts() {
        Object component = new Object();
        Class<?>[] contracts = { Comparable.class };
        when(delegate.register(component, contracts)).thenReturn(delegate);
        assertThat(builder.register(component, contracts)).isSameAs(builder);
        verify(delegate).register(component, contracts);
    }

    @Test
    void delegateRegisterObjectWithMapOfContracts() {
        Object component = new Object();
        Map<Class<?>, Integer> contracts = new HashMap<>();
        when(delegate.register(component, contracts)).thenReturn(delegate);
        assertThat(builder.register(component, contracts)).isSameAs(builder);
        verify(delegate).register(component, contracts);
    }

}
