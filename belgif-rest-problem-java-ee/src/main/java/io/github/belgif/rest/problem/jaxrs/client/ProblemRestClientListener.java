package io.github.belgif.rest.problem.jaxrs.client;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientListener;

import io.github.belgif.rest.problem.jaxrs.ProblemObjectMapperContextResolver;

/**
 * Listener that enables problem support for MicroProfile REST Clients.
 *
 * Note: Using a RestClientListener instead of RestClientBuilderListener to allow users to register
 * their own custom ObjectMapperContextResolver.
 *
 * @see RestClientListener
 * @see RestClientBuilder
 */
public class ProblemRestClientListener implements RestClientListener {

    @Override
    public void onNewClient(Class<?> serviceInterface, RestClientBuilder builder) {
        builder.register(ClientProblemObjectMapperContextResolver.class);
        builder.register(ProblemResponseExceptionMapper.class);
    }

    // Workaround for a weird bug in JBoss EAP XP MicroProfile REST client:
    // java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
    // at org.jboss.resteasy.spi.ResteasyProviderFactory.addContextResolver(ResteasyProviderFactory.java:1518)
    // If the ContextResolver class is not annotated with @Provider it works as expected.
    @Priority(Priorities.USER + 200)
    public static class ClientProblemObjectMapperContextResolver extends ProblemObjectMapperContextResolver {
    }

}
