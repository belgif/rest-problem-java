package io.github.belgif.rest.problem.spring.i18n;

import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.spring.ProblemConfigurationProperties;

/**
 * Enables or disables problem I18N support based on configuration property.
 */
@Component
public class I18NConfigurator {

    public I18NConfigurator(ProblemConfigurationProperties configuration) {
        I18N.setEnabled(configuration.isI18n());
    }

}
