package io.github.belgif.rest.problem.ee.jaxrs.client;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

/**
 * CDI producer for problem-enabled JAX-RS ClientBuilder and Client.
 *
 * @see ClientBuilder
 * @see Client
 */
@ApplicationScoped
public class ProblemClientBuilderProducer {

    @Produces
    public ClientBuilder problemClientBuilder() {
        return new ProblemClientBuilder(ClientBuilder.newBuilder());
    }

    @Produces
    public Client problemClient() {
        return ProblemSupport.enable(ClientBuilder.newClient());
    }

}
