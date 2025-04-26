package io.github.belgif.rest.problem.quarkus;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.quarkus.runtime.Startup;

/**
 * Configures the problem configuration based on MicroProfile Config properties.
 */
@ApplicationScoped
@Startup
public class QuarkusProblemConfigurator {

    @Inject
    public QuarkusProblemConfigurator(
            @ConfigProperty(name = ProblemConfig.PROPERTY_I18N) Optional<Boolean> i18n,
            @ConfigProperty(name = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES) Optional<Boolean> extIssueTypes,
            @ConfigProperty(name = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY) Optional<Boolean> extInputsArray) {
        ProblemConfig.setI18nEnabled(i18n.orElse(ProblemConfig.DEFAULT_I18N));
        ProblemConfig.setExtIssueTypesEnabled(extIssueTypes.orElse(ProblemConfig.DEFAULT_EXT_ISSUE_TYPES));
        ProblemConfig.setExtInputsArrayEnabled(extInputsArray.orElse(ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY));
    }

}
