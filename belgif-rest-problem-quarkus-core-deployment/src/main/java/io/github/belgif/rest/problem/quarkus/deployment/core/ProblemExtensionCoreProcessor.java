package io.github.belgif.rest.problem.quarkus.deployment.core;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.DotName;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.CombinedIndexBuildItem;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.deployment.builditem.nativeimage.NativeImageResourceBundleBuildItem;
import io.quarkus.deployment.builditem.nativeimage.ReflectiveClassBuildItem;

/**
 * BuildSteps for belgif-rest-problem-quarkus-core extension needed to support native mode.
 */
public class ProblemExtensionCoreProcessor {

    private static final String FEATURE = "belgif-rest-problem-core";

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
                .reason(ProblemExtensionCoreProcessor.class.getName())
                .constructors().methods().fields()
                .build();
    }

    @BuildStep
    NativeImageResourceBundleBuildItem i18nBundle() {
        return new NativeImageResourceBundleBuildItem(I18N.DEFAULT_BUNDLE);
    }

}
