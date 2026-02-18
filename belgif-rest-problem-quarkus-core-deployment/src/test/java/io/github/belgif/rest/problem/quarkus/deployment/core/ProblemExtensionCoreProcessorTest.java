package io.github.belgif.rest.problem.quarkus.deployment.core;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collections;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;
import org.jboss.jandex.IndexView;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

class ProblemExtensionCoreProcessorTest {

    private final ProblemExtensionCoreProcessor processor = new ProblemExtensionCoreProcessor();

    @Test
    void feature() {
        assertThat(processor.feature().getName()).isEqualTo("belgif-rest-problem-core");
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

}
