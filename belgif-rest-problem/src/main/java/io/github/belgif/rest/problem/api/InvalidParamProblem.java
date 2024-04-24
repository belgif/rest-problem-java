package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonSetter;

/**
 * Abstract base class for problems with invalid parameter(s).
 *
 * @see InvalidParam
 * @deprecated use {@link InputValidationProblem}
 */
@Deprecated
public abstract class InvalidParamProblem extends Problem {

    private static final long serialVersionUID = 1L;

    private final List<InvalidParam> invalidParams = new ArrayList<>();

    public InvalidParamProblem(URI type, URI href, String title, int status) {
        super(type, href, title, status);
    }

    public InvalidParamProblem(URI type, String title, int status) {
        super(type, title, status);
    }

    public InvalidParamProblem(URI type, URI href, String title, int status, Throwable cause) {
        super(type, href, title, status, cause);
    }

    public InvalidParamProblem(URI type, String title, int status, Throwable cause) {
        super(type, title, status, cause);
    }

    public List<InvalidParam> getInvalidParams() {
        return Collections.unmodifiableList(invalidParams);
    }

    @JsonSetter
    public void setInvalidParams(List<InvalidParam> invalidParams) {
        this.invalidParams.clear();
        this.invalidParams.addAll(invalidParams);
    }

    public void setInvalidParams(InvalidParam... invalidParams) {
        this.invalidParams.clear();
        this.invalidParams.addAll(Arrays.asList(invalidParams));
    }

    public void addInvalidParam(InvalidParam invalidParam) {
        invalidParams.add(invalidParam);
    }

}