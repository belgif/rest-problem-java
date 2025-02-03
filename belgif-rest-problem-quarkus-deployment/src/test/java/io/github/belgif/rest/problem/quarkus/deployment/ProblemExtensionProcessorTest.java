package io.github.belgif.rest.problem.quarkus.deployment;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import jakarta.ws.rs.client.AsyncInvoker;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.ee.jaxrs.ProblemObjectMapperContextResolver;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemResponseExceptionMapper;
import io.github.belgif.rest.problem.ee.jaxrs.client.ProblemRestClientListener;
import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageProxyDefinitionBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ServiceProviderBuildItem;

class ProblemExtensionProcessorTest {

    private final ProblemExtensionProcessor processor = new ProblemExtensionProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("belgif-rest-problem");
    }

    @Test
    void registerProblemModelClassesForReflection() {
        CombinedIndexBuildItem indexBuildItem = mock(CombinedIndexBuildItem.class);
        IndexView indexView = mock(IndexView.class);
        ClassInfo classInfo = mock(ClassInfo.class);
        when(indexBuildItem.getIndex()).thenReturn(indexView);
        when(classInfo.name()).thenReturn(DotName.createSimple("io.github.belgif.rest.problem.MyProblem"));
        when(indexView.getAllKnownSubclasses(Problem.class)).thenReturn(Collections.singletonList(classInfo));
        ReflectiveClassBuildItem result = processor.registerProblemModelClassesForReflection(indexBuildItem);
        assertThat(result.getClassNames()).containsExactly(Problem.class.getName(),
                InputValidationIssue.class.getName(), Input.class.getName(), InEnum.class.getName(),
                "io.github.belgif.rest.problem.MyProblem");
        assertThat(result.isConstructors()).isTrue();
        assertThat(result.isMethods()).isTrue();
        assertThat(result.isFields()).isTrue();
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
    void i18nBundle() {
        NativeImageResourceBundleBuildItem bundle = processor.i18nBundle();
        assertThat(bundle.getBundleName()).isEqualTo(I18N.DEFAULT_BUNDLE);
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
