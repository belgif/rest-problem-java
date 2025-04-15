package io.github.belgif.rest.problem.quarkus;

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
            @ConfigProperty(name = ProblemConfig.PROPERTY_I18N, defaultValue = "true") boolean i18n,
            @ConfigProperty(name = ProblemConfig.PROPERTY_EXT_ISSUE_TYPES,
                    defaultValue = "false") boolean extIssueTypes,
            @ConfigProperty(name = ProblemConfig.PROPERTY_EXT_INPUTS_ARRAY,
                    defaultValue = "false") boolean extInputsArray) {
        ProblemConfig.setI18nEnabled(i18n);
        ProblemConfig.setExtIssueTypesEnabled(extIssueTypes);
        ProblemConfig.setExtInputsArrayEnabled(extInputsArray);
    }

}
