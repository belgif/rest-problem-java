package io.github.belgif.rest.problem.quarkus.i18n;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.belgif.rest.problem.i18n.I18N;
import io.quarkus.runtime.Startup;

/**
 * Enables or disables problem I18N support based on MicroProfile Config property.
 */
@ApplicationScoped
@Startup
public class I18NConfigurator {

    @Inject
    public I18NConfigurator(@ConfigProperty(name = I18N.I18N_FLAG, defaultValue = "true") boolean enabled) {
        I18N.setEnabled(enabled);
    }

}
