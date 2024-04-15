package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.api.RetryAfterClientProblem;

/**
 * HTTP 429: Too Many Requests (https://www.belgif.be/specification/rest/api-guide/problems/tooManyRequests.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#too-many-requests">
 *      https://www.belgif.be/specification/rest/api-guide/#too-many-requests</a>
 */
@ProblemType(TooManyRequestsProblem.TYPE)
public class TooManyRequestsProblem extends RetryAfterClientProblem {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:tooManyRequests";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/tooManyRequests.html");

    /**
     * The title.
     */
    public static final String TITLE = "Too Many Requests";

    /**
     * The status.
     */
    public static final int STATUS = 429;

    private static final long serialVersionUID = 1L;

    private Long limit;

    public TooManyRequestsProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

}
