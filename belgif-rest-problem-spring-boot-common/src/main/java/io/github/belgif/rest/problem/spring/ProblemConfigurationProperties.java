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

    private Boolean serverSideEnabled = null;

    private List<String> scanAdditionalProblemPackages = new ArrayList<>();

    private Boolean i18nEnabled = null;

    private Boolean stackTraceEnabled = null;

    public void setServerSideEnabled(Boolean serverSideEnabled) {
        this.serverSideEnabled = serverSideEnabled;
    }

    public void setScanAdditionalProblemPackages(List<String> scanAdditionalProblemPackages) {
        this.scanAdditionalProblemPackages = scanAdditionalProblemPackages;
    }

    public List<String> getScanAdditionalProblemPackages() {
        return scanAdditionalProblemPackages;
    }

    public void setI18nEnabled(boolean i18nEnabled) {
        this.i18nEnabled = i18nEnabled;
    }

    public void setStackTraceEnabled(Boolean stackTraceEnabled) {
        this.stackTraceEnabled = stackTraceEnabled;
    }

    @Override
    public void afterPropertiesSet() {
        if (serverSideEnabled != null) {
            ProblemConfig.setServerSideEnabled(serverSideEnabled);
        }
        if (i18nEnabled != null) {
            ProblemConfig.setI18nEnabled(i18nEnabled);
        }
        if (stackTraceEnabled != null) {
            ProblemConfig.setStackTraceEnabled(stackTraceEnabled);
        }
    }

}
