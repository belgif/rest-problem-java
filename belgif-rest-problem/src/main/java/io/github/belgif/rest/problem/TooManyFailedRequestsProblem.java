package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Objects;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.api.RetryAfterClientProblem;

/**
 * HTTP 429: Too Many Failed Requests
 * (https://www.belgif.be/specification/rest/api-guide/problems/tooManyFailedRequests.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#too-many-failed-requests">
 *      https://www.belgif.be/specification/rest/api-guide/#too-many-failed-requests</a>
 */
@ProblemType(TooManyFailedRequestsProblem.TYPE)
public class TooManyFailedRequestsProblem extends RetryAfterClientProblem {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:tooManyFailedRequests";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/tooManyFailedRequests.html");

    /**
     * The title.
     */
    public static final String TITLE = "Too Many Failed Requests";

    /**
     * The status.
     */
    public static final int STATUS = 429;

    private static final long serialVersionUID = 1L;

    private Long limit;

    public TooManyFailedRequestsProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
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
        TooManyFailedRequestsProblem that = (TooManyFailedRequestsProblem) o;
        return Objects.equals(limit, that.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), limit);
    }

}
