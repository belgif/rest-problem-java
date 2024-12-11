package io.github.belgif.rest.problem.quarkus;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.AdditionalMatchers.*;
import static org.mockito.Mockito.*;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.build.compatible.spi.ClassConfig;
import jakarta.enterprise.inject.build.compatible.spi.Parameters;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticBeanBuilder;
import jakarta.enterprise.inject.build.compatible.spi.SyntheticComponents;
import jakarta.enterprise.lang.model.declarations.ClassInfo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;
import io.github.belgif.rest.problem.quarkus.QuarkusProblemExtension.QuarkusProblemTypeRegistryCreator;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(MockitoExtension.class)
class QuarkusProblemExtensionTest {

    private final QuarkusProblemExtension extension = new QuarkusProblemExtension();

    @Mock
    private SyntheticComponents syntheticComponents;

    @Mock(answer = Answers.RETURNS_SELF)
    private SyntheticBeanBuilder<QuarkusProblemTypeRegistry> syntheticBeanBuilder;

    @Mock
    private Parameters parameters;

    @Test
    void empty() {
        when(syntheticComponents.addBean(QuarkusProblemTypeRegistry.class)).thenReturn(syntheticBeanBuilder);
        extension.createProblemTypeRegistry(syntheticComponents);
        verify(syntheticBeanBuilder).type(ProblemTypeRegistry.class);
        verify(syntheticBeanBuilder).name("problemTypeRegistry");
        verify(syntheticBeanBuilder).scope(ApplicationScoped.class);
        verify(syntheticBeanBuilder).createWith(QuarkusProblemTypeRegistryCreator.class);
        verify(syntheticBeanBuilder).withParam(eq("problemTypes"), aryEq(new ClassInfo[0]));

        when(parameters.get("problemTypes", Class[].class)).thenReturn(new Class[0]);
        QuarkusProblemTypeRegistry registry = new QuarkusProblemTypeRegistryCreator().create(null, parameters);
        assertThat(registry.getProblemTypes()).isEmpty();
    }

    @Test
    void detectProblemTypes() {
        ClassConfig classConfig1 = mock(ClassConfig.class);
        ClassInfo classInfo1 = mock(ClassInfo.class);
        when(classConfig1.info()).thenReturn(classInfo1);
        extension.detectProblemType(classConfig1);
        verify(classConfig1).removeAllAnnotations();

        ClassConfig classConfig2 = mock(ClassConfig.class);
        ClassInfo classInfo2 = mock(ClassInfo.class);
        when(classConfig2.info()).thenReturn(classInfo2);
        extension.detectProblemType(classConfig2);
        verify(classConfig2).removeAllAnnotations();

        when(syntheticComponents.addBean(QuarkusProblemTypeRegistry.class)).thenReturn(syntheticBeanBuilder);
        extension.createProblemTypeRegistry(syntheticComponents);
        verify(syntheticBeanBuilder).type(ProblemTypeRegistry.class);
        verify(syntheticBeanBuilder).name("problemTypeRegistry");
        verify(syntheticBeanBuilder).scope(ApplicationScoped.class);
        verify(syntheticBeanBuilder).createWith(QuarkusProblemTypeRegistryCreator.class);
        verify(syntheticBeanBuilder).withParam(eq("problemTypes"), aryEq(new ClassInfo[] { classInfo1, classInfo2 }));

        when(parameters.get("problemTypes", Class[].class))
                .thenReturn(new Class[] { BadRequestProblem.class, InternalServerErrorProblem.class });
        QuarkusProblemTypeRegistry registry = new QuarkusProblemTypeRegistryCreator().create(null, parameters);
        assertThat(registry.getProblemTypes()).hasSize(2);
    }

}
