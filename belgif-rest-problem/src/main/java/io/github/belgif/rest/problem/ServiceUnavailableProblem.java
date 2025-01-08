package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.FluentRetryAfterProblem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.api.RetryAfterServerProblem;

/**
 * HTTP 503: Service Unavailable (https://www.belgif.be/specification/rest/api-guide/problems/serviceUnavailable.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#service-unavailable">
 *      https://www.belgif.be/specification/rest/api-guide/#service-unavailable</a>
 */
@ProblemType(ServiceUnavailableProblem.TYPE)
public class ServiceUnavailableProblem extends RetryAfterServerProblem
        implements FluentRetryAfterProblem<ServiceUnavailableProblem> {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:serviceUnavailable";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/serviceUnavailable.html");

    /**
     * The title.
     */
    public static final String TITLE = "Service is unavailable";

    /**
     * The status.
     */
    public static final int STATUS = 503;

    private static final long serialVersionUID = 1L;

    public ServiceUnavailableProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public ServiceUnavailableProblem(Throwable e) {
        super(TYPE_URI, HREF, TITLE, STATUS, e);
    }

}
