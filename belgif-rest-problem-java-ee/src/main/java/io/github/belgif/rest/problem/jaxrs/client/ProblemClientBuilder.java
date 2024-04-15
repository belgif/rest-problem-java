package io.github.belgif.rest.problem.jaxrs.client;

import java.security.KeyStore;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.Configuration;

/**
 * ClientBuilder delegate wrapper that enables WrappedProblem exception mapping on the created Client instance.
 */
public class ProblemClientBuilder extends ClientBuilder {

    private final ClientBuilder delegate;

    public ProblemClientBuilder(ClientBuilder delegate) {
        this.delegate = delegate;
    }

    @Override
    public Client build() {
        return ProblemSupport.enable(delegate.build());
    }

    // all other methods below simply delegate to the wrapped ClientBuilder

    @Override
    public ClientBuilder withConfig(Configuration config) {
        return delegate.withConfig(config);
    }

    @Override
    public ClientBuilder sslContext(SSLContext sslContext) {
        return delegate.sslContext(sslContext);
    }

    @Override
    public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
        return delegate.keyStore(keyStore, password);
    }

    @Override
    public ClientBuilder keyStore(KeyStore keyStore, String password) {
        return delegate.keyStore(keyStore, password);
    }

    @Override
    public ClientBuilder trustStore(KeyStore trustStore) {
        return delegate.trustStore(trustStore);
    }

    @Override
    public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
        return delegate.hostnameVerifier(verifier);
    }

    @Override
    public ClientBuilder executorService(ExecutorService executorService) {
        return delegate.executorService(executorService);
    }

    @Override
    public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        return delegate.scheduledExecutorService(scheduledExecutorService);
    }

    @Override
    public ClientBuilder connectTimeout(long timeout, TimeUnit unit) {
        return delegate.connectTimeout(timeout, unit);
    }

    @Override
    public ClientBuilder readTimeout(long timeout, TimeUnit unit) {
        return delegate.readTimeout(timeout, unit);
    }

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    @Override
    public ClientBuilder property(String name, Object value) {
        return delegate.property(name, value);
    }

    @Override
    public ClientBuilder register(Class<?> componentClass) {
        return delegate.register(componentClass);
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, int priority) {
        return delegate.register(componentClass, priority);
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
        return delegate.register(componentClass, contracts);
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        return delegate.register(componentClass, contracts);
    }

    @Override
    public ClientBuilder register(Object component) {
        return delegate.register(component);
    }

    @Override
    public ClientBuilder register(Object component, int priority) {
        return delegate.register(component, priority);
    }

    @Override
    public ClientBuilder register(Object component, Class<?>... contracts) {
        return delegate.register(component, contracts);
    }

    @Override
    public ClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
        return delegate.register(component, contracts);
    }

}
