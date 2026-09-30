package io.github.belgif.rest.problem;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.github.belgif.rest.problem.api.FluentProblem;
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
public class DefaultProblem extends Problem implements FluentProblem<DefaultProblem> {

    private static final long serialVersionUID = 1L;

    @JsonCreator
    public DefaultProblem(@JsonProperty(value = "type", access = JsonProperty.Access.READ_WRITE) URI type,
            @JsonProperty(value = "href", access = JsonProperty.Access.READ_WRITE) URI href,
            @JsonProperty(value = "title", access = JsonProperty.Access.READ_WRITE) String title,
            @JsonProperty(value = "status", access = JsonProperty.Access.READ_WRITE) Integer status) {
        // fallback to 0 if status is absent, see https://github.com/belgif/rest-problem-java/issues/246
        super(type, href, title, status == null ? 0 : status);
    }

}
