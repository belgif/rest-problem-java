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

import org.eclipse.microprofile.rest.client.spi.RestClientListener;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.jaxrs.ProblemFeature;
import io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener;
import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.arc.deployment.AdditionalBeanBuildItem;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

/**
 * BuildSteps for belgif-rest-problem-quarkus extension needed to support native mode.
 */
public class ProblemExtensionProcessor {

    private static final String FEATURE = "belgif-rest-problem";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    ReflectiveClassBuildItem registerProblemModelClassesForReflection(CombinedIndexBuildItem index) {
        List<String> problemModelClasses = new ArrayList<>();
        problemModelClasses.add(Problem.class.getName());
        problemModelClasses.add(InputValidationIssue.class.getName());
        problemModelClasses.add(Input.class.getName());
        problemModelClasses.add(InEnum.class.getName());
        problemModelClasses.addAll(index.getIndex().getAllKnownSubclasses(Problem.class).stream()
                .map(ClassInfo::name)
                .map(DotName::toString)
                .collect(Collectors.toList()));
        return ReflectiveClassBuildItem.builder(problemModelClasses.toArray(new String[0]))
                .reason(ProblemExtensionProcessor.class.getName())
                .constructors().methods().fields()
                .build();
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
    NativeImageResourceBundleBuildItem i18nBundle() {
        return new NativeImageResourceBundleBuildItem(I18N.DEFAULT_BUNDLE);
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

    @BuildStep
    AdditionalBeanBuildItem registerProblemFeatures() {
        AdditionalBeanBuildItem.Builder builder = AdditionalBeanBuildItem.builder();
        builder.setUnremovable();
        for (Class<?> providerClass : ProblemFeature.DEFAULT_PROVIDER_CLASSES) {
            builder.addBeanClass(providerClass);
        }
        return builder.build();
    }

    @BuildStep
    ReflectiveClassBuildItem registerProblemFeaturesForReflection() {
        return ReflectiveClassBuildItem.builder(Arrays.stream(ProblemFeature.DEFAULT_PROVIDER_CLASSES)
                .map(Class::getName)
                .toArray(String[]::new))
                .constructors().methods().fields()
                .build();
    }

}
