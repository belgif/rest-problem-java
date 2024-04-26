package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * Default generic problem implementation class in case no specific type mapping was found.
 *
 * <p>
 * This type is used when deserializing a problem for which no @{@link ProblemType} annotated {@link Problem} class
 * with the matching problem type URI has been found on the classpath.
 * </p>
 */
public class DefaultProblem extends Problem {

    private static final long serialVersionUID = 1L;

    private final Map<String, Object> additionalProperties = new HashMap<>();

    @JsonCreator
    public DefaultProblem(@JsonProperty("type") URI type, @JsonProperty("href") URI href,
            @JsonProperty("title") String title, @JsonProperty("status") int status) {
        super(type, href, title, status);
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
        if (!super.equals(o)) {
            return false;
        }
        DefaultProblem that = (DefaultProblem) o;
        return Objects.equals(additionalProperties, that.additionalProperties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), additionalProperties);
    }

}
