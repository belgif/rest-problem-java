package io.github.belgif.rest.problem.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties configurations for the belgif-rest-problem-spring library.
 */
@ConfigurationProperties(prefix = "io.github.belgif.rest.problem")
public class ProblemConfigurationProperties {

    private List<String> scanAdditionalProblemPackages = new ArrayList<>();

    public void setScanAdditionalProblemPackages(List<String> scanAdditionalProblemPackages) {
        this.scanAdditionalProblemPackages = scanAdditionalProblemPackages;
    }

    public List<String> getScanAdditionalProblemPackages() {
        return scanAdditionalProblemPackages;
    }

    public void setI18n(boolean i18n) {
        ProblemConfig.setI18nEnabled(i18n);
    }

}
