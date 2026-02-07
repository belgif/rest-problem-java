package io.github.belgif.rest.problem.ee.jaxrs.client;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Proxy;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.junit.jupiter.api.Test;

class ProblemClientBuilderProducerTest {

    @Test
    void problemClientBuilder() {
        ClientBuilder builder = new ProblemClientBuilderProducer().problemClientBuilder();
        assertThat(builder).isInstanceOf(ProblemClientBuilder.class);
    }

    @Test
    void problemClient() {
        Client client = new ProblemClientBuilderProducer().problemClient();
        assertThat(Proxy.isProxyClass(client.getClass())).isTrue();
        assertThat(Proxy.getInvocationHandler(client))
                .isInstanceOf(ProblemSupport.ClientInvocationHandler.class);
    }

}
