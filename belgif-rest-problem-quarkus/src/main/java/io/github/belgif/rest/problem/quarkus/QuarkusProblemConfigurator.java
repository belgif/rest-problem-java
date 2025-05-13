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
            @ConfigProperty(
                    name = ProblemConfig.PROPERTY_I18N_ENABLED) Optional<Boolean> i18nEnabled,
            @ConfigProperty(
                    name = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES_ENABLED) Optional<Boolean> extIssueTypesEnabled,
            @ConfigProperty(
                    name = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY_ENABLED) Optional<Boolean> extInputsArrayEnabled) {
        ProblemConfig.setI18nEnabled(i18nEnabled.orElse(
                ProblemConfig.DEFAULT_I18N_ENABLED));
        ProblemConfig.setExtIssueTypesEnabled(extIssueTypesEnabled.orElse(
                ProblemConfig.DEFAULT_EXT_ISSUE_TYPES_ENABLED));
        ProblemConfig.setExtInputsArrayEnabled(extInputsArrayEnabled.orElse(
                ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY_ENABLED));
    }

}
