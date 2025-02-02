package io.github.belgif.rest.problem.quarkus.deployment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

public class ProblemExtensionProcessor {

    private static final String FEATURE = "belgif-rest-problem";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    List<ReflectiveClassBuildItem> registerProblemClassesForReflection(CombinedIndexBuildItem index) {
        List<ReflectiveClassBuildItem> items = new ArrayList<>();
        items.add(reflectiveClass(Problem.class.getName()));
        items.add(reflectiveClass(InputValidationIssue.class.getName()));
        items.add(reflectiveClass(Input.class.getName()));
        items.add(reflectiveClass(InEnum.class.getName()));
        items.addAll(index.getIndex().getAllKnownSubclasses(Problem.class).stream()
                .map(ClassInfo::name)
                .map(DotName::toString)
                .map(this::reflectiveClass)
                .collect(Collectors.toList()));
        return items;
    }

    private ReflectiveClassBuildItem reflectiveClass(String className) {
        return ReflectiveClassBuildItem.builder(className).constructors().methods().fields().build();
    }

    @BuildStep
    List<NativeImageProxyDefinitionBuildItem> jaxrsClientProxies() {
        return Arrays.asList(
                new NativeImageProxyDefinitionBuildItem(Client.class.getName()),
                new NativeImageProxyDefinitionBuildItem(WebTarget.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Invocation.Builder.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Invocation.class.getName()),
                new NativeImageProxyDefinitionBuildItem(AsyncInvoker.class.getName()),
                new NativeImageProxyDefinitionBuildItem(Future.class.getName()));
    }

    @BuildStep
    NativeImageResourceBundleBuildItem i18nBundle() {
        return new NativeImageResourceBundleBuildItem("io.github.belgif.rest.problem.Messages");
    }

    @BuildStep
    ServiceProviderBuildItem restClientListenerService() {
        return new ServiceProviderBuildItem(
                "org.eclipse.microprofile.rest.client.spi.RestClientListener",
                "io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener");
    }

    @BuildStep
    List<ReflectiveClassBuildItem> restClientListenerReflection() {
        return Arrays.asList(reflectiveClass("io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener" +
                "$ClientProblemObjectMapperContextResolver"),
                reflectiveClass("io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapperContextResolver"),
                reflectiveClass("io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper"));
    }

}
