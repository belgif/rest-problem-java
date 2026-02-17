package io.github.belgif.rest.problem.quarkus.deployment.server;

import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.builditem.FeatureBuildItem;

/**
 * BuildSteps for belgif-rest-problem-quarkus-server extension needed to support native mode.
 */
public class ProblemExtensionServerProcessor {

    private static final String FEATURE = "belgif-rest-problem-server";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

}
