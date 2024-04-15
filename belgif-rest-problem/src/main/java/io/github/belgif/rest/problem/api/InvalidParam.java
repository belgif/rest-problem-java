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
 * @see InvalidParamProblem
 * @deprecated use {@link InputValidationIssue}
 */
@JsonInclude(Include.NON_EMPTY)
@Deprecated
public class InvalidParam {

    public static final String ISSUE_TYPE_SCHEMA_VIOLATION = "schemaViolation";

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
    public int hashCode() {
        return Objects.hash(in, name, reason, value, issueType);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof InvalidParam)) {
            return false;
        }
        InvalidParam other = (InvalidParam) obj;
        return in == other.in && Objects.equals(name, other.name) && Objects.equals(reason, other.reason)
                && Objects.equals(value, other.value) && Objects.equals(issueType, other.issueType);
    }

    @Override
    public String toString() {
        return "InvalidParam [in=" + in + ", name=" + name + ", reason=" + reason
                + ", value=" + value + ", issueType=" + issueType + "]";
    }

}
