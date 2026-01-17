package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.Provider;

import org.eclipse.microprofile.config.ConfigProvider;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.github.belgif.rest.problem.ee.internal.Platform;

@Provider
public class ConditionalProblemFeature extends ProblemFeature {

    @Override
    public boolean configure(FeatureContext featureContext) {
        if (isEnabled()) {
            return super.configure(featureContext);
        } else {
            return false;
        }
    }

    private static boolean isEnabled() {
        if (Platform.isQuarkus()) {
            // QuarkusProblemConfigurator runs after JAX-RS feature configuration, so we cannot
            // use ProblemConfig.isServerSideEnabled() here as it won't have been initialized yet
            return ConfigProvider.getConfig()
                    .getOptionalValue(ProblemConfig.PROPERTY_SERVER_SIDE_ENABLED, Boolean.class)
                    .orElse(ProblemConfig.isServerSideEnabled());
        } else {
            return ProblemConfig.isServerSideEnabled();
        }
    }

}
