package io.github.belgif.rest.problem.ee.jaxrs.client;

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

    @Override
    public Configuration getConfiguration() {
        return delegate.getConfiguration();
    }

    // all other methods below simply delegate to the wrapped ClientBuilder, but return the ProblemClientBuilder

    @Override
    public ClientBuilder withConfig(Configuration config) {
        delegate.withConfig(config);
        return this;
    }

    @Override
    public ClientBuilder sslContext(SSLContext sslContext) {
        delegate.sslContext(sslContext);
        return this;
    }

    @Override
    public ClientBuilder keyStore(KeyStore keyStore, char[] password) {
        delegate.keyStore(keyStore, password);
        return this;
    }

    @Override
    public ClientBuilder keyStore(KeyStore keyStore, String password) {
        delegate.keyStore(keyStore, password);
        return this;
    }

    @Override
    public ClientBuilder trustStore(KeyStore trustStore) {
        delegate.trustStore(trustStore);
        return this;
    }

    @Override
    public ClientBuilder hostnameVerifier(HostnameVerifier verifier) {
        delegate.hostnameVerifier(verifier);
        return this;
    }

    @Override
    public ClientBuilder executorService(ExecutorService executorService) {
        delegate.executorService(executorService);
        return this;
    }

    @Override
    public ClientBuilder scheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
        delegate.scheduledExecutorService(scheduledExecutorService);
        return this;
    }

    @Override
    public ClientBuilder connectTimeout(long timeout, TimeUnit unit) {
        delegate.connectTimeout(timeout, unit);
        return this;
    }

    @Override
    public ClientBuilder readTimeout(long timeout, TimeUnit unit) {
        delegate.readTimeout(timeout, unit);
        return this;
    }

    @Override
    public ClientBuilder property(String name, Object value) {
        delegate.property(name, value);
        return this;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass) {
        delegate.register(componentClass);
        return this;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, int priority) {
        delegate.register(componentClass, priority);
        return this;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Class<?>... contracts) {
        delegate.register(componentClass, contracts);
        return this;
    }

    @Override
    public ClientBuilder register(Class<?> componentClass, Map<Class<?>, Integer> contracts) {
        delegate.register(componentClass, contracts);
        return this;
    }

    @Override
    public ClientBuilder register(Object component) {
        delegate.register(component);
        return this;
    }

    @Override
    public ClientBuilder register(Object component, int priority) {
        delegate.register(component, priority);
        return this;
    }

    @Override
    public ClientBuilder register(Object component, Class<?>... contracts) {
        delegate.register(component, contracts);
        return this;
    }

    @Override
    public ClientBuilder register(Object component, Map<Class<?>, Integer> contracts) {
        delegate.register(component, contracts);
        return this;
    }

}
