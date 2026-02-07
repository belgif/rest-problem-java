package io.github.belgif.rest.problem.quarkus.deployment.client;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import org.eclipse.microprofile.rest.client.spi.RestClientListener;

import io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

/**
 * BuildSteps for belgif-rest-problem-quarkus-client extension needed to support native mode.
 */
public class ProblemExtensionClientProcessor {

    private static final String FEATURE = "belgif-rest-problem-client";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<NativeImageProxyDefinitionBuildItem> registerJaxRsClientProxies() {
        return Arrays.asList(
                new NativeImageProxyDefinitionBuildItem(Client.class.getName()),
                new NativeImageProxyDefinitionBuildItem(WebTarget.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Invocation.Builder.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Invocation.class.getName()),
                new NativeImageProxyDefinitionBuildItem(AsyncInvoker.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Future.class.getName()));
    }

    @BuildStep
    ServiceProviderBuildItem registerRestClientListenerServiceProvider() {
        return new ServiceProviderBuildItem(RestClientListener.class.getName(),
                ProblemRestClientListener.class.getName());
    }

    @BuildStep
    ReflectiveClassBuildItem registerRestClientListenerForReflection() {
        return ReflectiveClassBuildItem.builder(
                ProblemRestClientListener.class.getName(),
                ProblemRestClientListener.ClientProblemObjectMapperContextResolver.class.getName(),
                ProblemObjectMapperContextResolver.class.getName(),
                ProblemResponseExceptionMapper.class.getName())
                .constructors().methods().fields()
                .build();
    }

}
