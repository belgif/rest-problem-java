package io.github.belgif.rest.problem.spring;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties extension configurations for the belgif-rest-problem-spring library.
 */
@ConfigurationProperties(prefix = "io.github.belgif.rest.problem.ext")
public class ProblemExtConfigurationProperties implements InitializingBean {

    private boolean issueTypes = ProblemConfig.DEFAULT_EXT_ISSUE_TYPES;

    private boolean inputsArray = ProblemConfig.DEFAULT_EXT_INPUTS_ARRAY;

    public void setIssueTypes(boolean issueTypes) {
        this.issueTypes = issueTypes;
    }

    public void setInputsArray(boolean inputsArray) {
        this.inputsArray = inputsArray;
    }

    @Override
    public void afterPropertiesSet() {
        ProblemConfig.setExtIssueTypesEnabled(issueTypes);
        ProblemConfig.setExtInputsArrayEnabled(inputsArray);
    }

}
