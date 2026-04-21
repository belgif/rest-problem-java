package io.github.belgif.rest.problem.ee.client.jaxrs;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.spi.RestClientListener;

import io.github.belgif.rest.problem.ee.core.jaxrs.JaxRsUtil;
import io.github.belgif.rest.problem.ee.core.util.Platform;

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
        JaxRsUtil.register(builder, ProblemResponseExceptionMapper.class);
        if (!Platform.isQuarkus()) {
            JaxRsUtil.register(builder, ClientProblemObjectMapperContextResolver.class);
        }
    }

}
