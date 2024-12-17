package io.github.belgif.rest.problem.api;

import java.util.List;

/**
 * Provides default methods with fluent InputValidationProblem properties (issues).
 *
 * @param <SELF> the concrete InputValidationProblem self-type
 */
@SuppressWarnings("unchecked")
public interface FluentInputValidationProblem<SELF extends InputValidationProblem & FluentInputValidationProblem<SELF>>
        extends FluentProblem<SELF> {

    void setIssues(InputValidationIssue... issues);

    void setIssues(List<InputValidationIssue> issues);

    default SELF issues(InputValidationIssue... issues) {
        setIssues(issues);
        return (SELF) this;
    }

    default SELF issues(List<InputValidationIssue> issues) {
        setIssues(issues);
        return (SELF) this;
    }

}
