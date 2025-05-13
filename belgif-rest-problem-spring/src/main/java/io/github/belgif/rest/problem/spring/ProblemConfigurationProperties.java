package io.github.belgif.rest.problem.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties configurations for the belgif-rest-problem-spring library.
 */
@ConfigurationProperties(prefix = "io.github.belgif.rest.problem")
public class ProblemConfigurationProperties implements InitializingBean {

    private List<String> scanAdditionalProblemPackages = new ArrayList<>();

    private boolean i18nEnabled = ProblemConfig.DEFAULT_I18N_ENABLED;

    public void setScanAdditionalProblemPackages(List<String> scanAdditionalProblemPackages) {
        this.scanAdditionalProblemPackages = scanAdditionalProblemPackages;
    }

    public List<String> getScanAdditionalProblemPackages() {
        return scanAdditionalProblemPackages;
    }

    public void setI18nEnabled(boolean i18nEnabled) {
        this.i18nEnabled = i18nEnabled;
    }

    @Override
    public void afterPropertiesSet() {
        ProblemConfig.setI18nEnabled(i18nEnabled);
    }

}
