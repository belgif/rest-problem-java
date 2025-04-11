package io.github.belgif.rest.problem.spring;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.github.belgif.rest.problem.config.ProblemConfig;

/**
 * Lists all supported application.properties extension configurations for the belgif-rest-problem-spring library.
 */
@ConfigurationProperties(prefix = "io.github.belgif.rest.problem.ext")
public class ProblemExtConfigurationProperties {

    public void setIssueTypes(boolean issueTypes) {
        ProblemConfig.setExtIssueTypesEnabled(issueTypes);
    }

    public void setInputsArray(boolean inputsArray) {
        ProblemConfig.setExtInputsArrayEnabled(inputsArray);
    }

}
