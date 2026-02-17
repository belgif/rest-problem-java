package io.github.belgif.rest.problem.quarkus.deployment.client;

import static org.assertj.core.api.Assertions.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class ProblemExtensionClientProcessorTest {

    private final ProblemExtensionClientProcessor processor = new ProblemExtensionClientProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("belgif-rest-problem-client");
    }

    @Test
    void registerJaxRsClientProxies() {
        List<NativeImageProxyDefinitionBuildItem> result = processor.registerJaxRsClientProxies();
        assertThat(result).extracting("classes").containsExactly(
                Collections.singletonList(Client.class.getName()),
                Collections.singletonList(WebTarget.class.getName()),
                Collections.singletonList(Invocation.Builder.class.getName()),
                Collections.singletonList(Invocation.class.getName()),
                Collections.singletonList(AsyncInvoker.class.getName()),
                Collections.singletonList(Future.class.getName()));
    }

    @Test
    void registerRestClientListenerServiceProvider() {
        ServiceProviderBuildItem serviceProvider = processor.registerRestClientListenerServiceProvider();
        assertThat(serviceProvider.serviceDescriptorFile())
                .isEqualTo("META-INF/services/org.eclipse.microprofile.rest.client.spi.RestClientListener");
        assertThat(serviceProvider.providers()).containsExactly(ProblemRestClientListener.class.getName());
    }

    @Test
    void registerRestClientListenerForReflection() {
        ReflectiveClassBuildItem result = processor.registerRestClientListenerForReflection();
        assertThat(result.getClassNames()).containsExactly(
                ProblemRestClientListener.class.getName(),
                ProblemRestClientListener.ClientProblemObjectMapperContextResolver.class.getName(),
                ProblemObjectMapperContextResolver.class.getName(),
                ProblemResponseExceptionMapper.class.getName());
        assertThat(result.isConstructors()).isTrue();
        assertThat(result.isMethods()).isTrue();
        assertThat(result.isFields()).isTrue();
    }

}
