package io.github.belgif.rest.problem.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties extension configurations for the belgif-rest-problem-spring library.
 */
@ConfigurationProperties(prefix = "io.github.belgif.rest.problem.ext")
public class ProblemExtConfigurationProperties implements InitializingBean {

    private Boolean issueTypesEnabled = null;

    private Boolean inputsArrayEnabled = null;

    public void setIssueTypesEnabled(boolean issueTypesEnabled) {
        this.issueTypesEnabled = issueTypesEnabled;
    }

    public void setInputsArrayEnabled(boolean inputsArrayEnabled) {
        this.inputsArrayEnabled = inputsArrayEnabled;
    }

    @Override
    public void afterPropertiesSet() {
        if (issueTypesEnabled != null) {
            ProblemConfig.setExtIssueTypesEnabled(issueTypesEnabled);
        }
        if (inputsArrayEnabled != null) {
            ProblemConfig.setExtInputsArrayEnabled(inputsArrayEnabled);
        }
    }

}
