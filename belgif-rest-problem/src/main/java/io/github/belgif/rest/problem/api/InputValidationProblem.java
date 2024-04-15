package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Abstract base class for input validation problems.
 *
 * Maps to InputValidationProblem in belgif/problem/v1/problem-v1.yaml.
 *
 * @see InputValidationIssue
 */
public abstract class InputValidationProblem extends ClientProblem {

    private static final long serialVersionUID = 1L;

    private final List<InputValidationIssue> issues = new ArrayList<>();

    protected InputValidationProblem(URI type, URI href, String title, int status) {
        super(type, href, title, status);
    }

    protected InputValidationProblem(URI type, String title, int status) {
        super(type, title, status);
    }

    protected InputValidationProblem(URI type, URI href, String title, int status, Throwable cause) {
        super(type, href, title, status, cause);
    }

    protected InputValidationProblem(URI type, String title, int status, Throwable cause) {
        super(type, title, status, cause);
    }

    public List<InputValidationIssue> getIssues() {
        return Collections.unmodifiableList(issues);
    }

    @JsonSetter
    public void setIssues(List<InputValidationIssue> issues) {
        this.issues.clear();
        this.issues.addAll(issues);
    }

    public void setIssues(InputValidationIssue... issues) {
        this.issues.clear();
        this.issues.addAll(Arrays.asList(issues));
    }

    public void addIssue(InputValidationIssue issue) {
        issues.add(issue);
    }

    @Override
    public String getMessage() {
        StringBuilder message = new StringBuilder(super.getMessage());
        for (InputValidationIssue issue : issues) {
            if (issue.getTitle() != null || issue.getDetail() != null) {
                if (issue.getTitle() != null && issue.getDetail() != null) {
                    message.append("\n- " + issue.getTitle() + ": " + issue.getDetail());
                } else if (issue.getTitle() != null) {
                    message.append("\n- " + issue.getTitle());
                } else if (issue.getDetail() != null) {
                    message.append("\n- " + issue.getDetail());
                }
            } else if (issue.getType() != null) {
                message.append("\n- " + issue.getType());
            }
        }
        return message.toString();
    }

}
