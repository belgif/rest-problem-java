package io.github.belgif.rest.problem.api;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import io.github.belgif.rest.problem.DefaultProblem;

/**
 * Abstract base class for problems (RFC 9457).
 *
 * Maps to Problem in belgif/problem/v1/problem-v1.yaml.
 */
@JsonTypeInfo(
        // this configures the existing "type" property as discriminator for polymorphic deserialization
        use = Id.NAME, include = As.EXISTING_PROPERTY, property = "type", visible = true,
        // when no problem type was mapped, fall back to the DefaultProblem class when deserializing
        defaultImpl = DefaultProblem.class)
@JsonIgnoreProperties(
        // we don't want to serialize these properties from the RuntimeException superclass
        value = { "cause", "stackTrace", "message", "localizedMessage", "suppressed" },
        // but we DO want to deserialize them (in additionalProperties),
        // so we don't lose any info e.g. when a problem contains a "message" field
        allowSetters = true,
        // we also want to ignore unknown properties when deserializing:
        // - for extensibility purposes, don't fail on new unknown JSON properties
        // - "status" is also an unknown property, because we don't have a setStatus()
        ignoreUnknown = true)
@JsonPropertyOrder(value = { "type", "href", "title", "status", "detail", "instance" })
@JsonInclude(Include.NON_EMPTY)
public abstract class Problem extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final URI type;
    private URI href;
    private final String title;
    private final Integer status;
    private String detail;
    private URI instance;

    private final Map<String, Object> additionalProperties = new HashMap<>();

    protected Problem(URI type, String title, Integer status) {
        this(type, null, title, status, null);
    }

    protected Problem(URI type, URI href, String title, Integer status) {
        this(type, href, title, status, null);
    }

    protected Problem(URI type, String title, Integer status, Throwable cause) {
        this(type, null, title, status, cause);
    }

    protected Problem(URI type, URI href, String title, Integer status, Throwable cause) {
        super(title, cause);
        this.type = type;
        this.href = href;
        this.title = title;
        this.status = status;
    }

    public Integer getStatus() {
        return status;
    }

    public URI getType() {
        return type;
    }

    public URI getHref() {
        return href;
    }

    public void setHref(URI href) {
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public URI getInstance() {
        return instance;
    }

    public void setInstance(URI instance) {
        this.instance = instance;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        additionalProperties.put(name, value);
    }

    /**
     * Returns the problem message, consisting of the problem title, followed by the detail message (if present).
     *
     * @return the problem message
     */
    @Override
    public String getMessage() {
        if (detail != null) {
            return title + ": " + detail;
        } else {
            return title;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Problem problem = (Problem) o;
        return Objects.equals(status, problem.status) && Objects.equals(type, problem.type) && Objects.equals(href, problem.href)
                && Objects.equals(title, problem.title) && Objects.equals(detail, problem.detail)
                && Objects.equals(instance, problem.instance)
                && Objects.equals(additionalProperties, problem.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, href, title, status, detail, instance, additionalProperties);
    }

}
