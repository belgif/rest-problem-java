package io.github.belgif.rest.problem.api;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Invalid parameter.
 *
 * @see InputValidationProblem
 * @deprecated use {@link InputValidationIssue}
 */
@JsonInclude(Include.NON_EMPTY)
@Deprecated
public class InvalidParam {

    private InEnum in;
    private String name;
    private String reason;
    private Object value;
    private String issueType;
    private final Map<String, Object> additionalProperties = new HashMap<>();

    public InvalidParam() {
    }

    public InvalidParam(InEnum in, String name) {
        this.in = in;
        this.name = name;
    }

    public InvalidParam(InEnum in, String name, Object value) {
        this.in = in;
        this.name = name;
        this.value = value;
    }

    public InvalidParam(InEnum in, String name, String reason, Object value) {
        this.in = in;
        this.name = name;
        this.reason = reason;
        this.value = value;
    }

    public InvalidParam(InEnum in, String name, String reason, Object value, String issueType) {
        this.in = in;
        this.name = name;
        this.reason = reason;
        this.value = value;
        this.issueType = issueType;
    }

    public InEnum getIn() {
        return in;
    }

    public void setIn(InEnum in) {
        this.in = in;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        InvalidParam that = (InvalidParam) o;
        return in == that.in && Objects.equals(name, that.name) && Objects.equals(reason, that.reason)
                && Objects.equals(value, that.value) && Objects.equals(issueType, that.issueType)
                && Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(in, name, reason, value, issueType, additionalProperties);
    }

    @Override
    public String toString() {
        return "InvalidParam [in=" + in + ", name=" + name + ", reason=" + reason
                + ", value=" + value + ", issueType=" + issueType + "]";
    }

}
