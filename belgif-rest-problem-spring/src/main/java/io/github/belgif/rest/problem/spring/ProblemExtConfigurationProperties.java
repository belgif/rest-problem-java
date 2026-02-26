package io.github.belgif.rest.problem.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties extension configurations for the belgif-rest-problem-spring library.
 */
@Component // not using @ConfigurationProperties to avoid Spring Boot dependency
public class ProblemExtConfigurationProperties implements InitializingBean {

    private Boolean issueTypesEnabled = null;

    private Boolean inputsArrayEnabled = null;

    @Value("${io.github.belgif.rest.problem.ext.issue-types-enabled:#{null}}")
    public void setIssueTypesEnabled(Boolean issueTypesEnabled) {
        this.issueTypesEnabled = issueTypesEnabled;
    }

    @Value("${io.github.belgif.rest.problem.ext.inputs-array-enabled:#{null}}")
    public void setInputsArrayEnabled(Boolean inputsArrayEnabled) {
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
