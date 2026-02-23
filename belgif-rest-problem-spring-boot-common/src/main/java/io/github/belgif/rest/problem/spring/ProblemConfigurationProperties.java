package io.github.belgif.rest.problem.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties configurations for the belgif-rest-problem-spring library.
 */
@Component // not using @ConfigurationProperties to avoid Spring Boot dependency
public class ProblemConfigurationProperties implements InitializingBean {

    private List<String> scanAdditionalProblemPackages = new ArrayList<>();

    private Boolean i18nEnabled = null;

    private Boolean stackTraceEnabled = null;

    @Value("${io.github.belgif.rest.problem.scan-additional-problem-packages:#{{}}}")
    public void setScanAdditionalProblemPackages(List<String> scanAdditionalProblemPackages) {
        this.scanAdditionalProblemPackages = scanAdditionalProblemPackages;
    }

    public List<String> getScanAdditionalProblemPackages() {
        return scanAdditionalProblemPackages;
    }

    @Value("${io.github.belgif.rest.problem.i18n-enabled:#{null}}")
    public void setI18nEnabled(Boolean i18nEnabled) {
        this.i18nEnabled = i18nEnabled;
    }

    @Value("${io.github.belgif.rest.problem.stack-trace-enabled:#{null}}")
    public void setStackTraceEnabled(Boolean stackTraceEnabled) {
        this.stackTraceEnabled = stackTraceEnabled;
    }

    @Override
    public void afterPropertiesSet() {
        if (i18nEnabled != null) {
            ProblemConfig.setI18nEnabled(i18nEnabled);
        }
        if (stackTraceEnabled != null) {
            ProblemConfig.setStackTraceEnabled(stackTraceEnabled);
        }
    }

}
