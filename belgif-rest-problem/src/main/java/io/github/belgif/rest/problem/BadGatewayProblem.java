package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.FluentProblem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.api.ServerProblem;

/**
 * HTTP 502: Bad Gateway (https://www.belgif.be/specification/rest/api-guide/problems/badGateway.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#bad-gateway">
 *      https://www.belgif.be/specification/rest/api-guide/#bad-gateway</a>
 */
@ProblemType(BadGatewayProblem.TYPE)
public class BadGatewayProblem extends ServerProblem implements FluentProblem<BadGatewayProblem> {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:badGateway";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/badGateway.html");

    /**
     * The title.
     */
    public static final String TITLE = "Bad Gateway";

    /**
     * The detail.
     */
    public static final String DETAIL = "Error in communication with upstream service";

    /**
     * The status.
     */
    public static final int STATUS = 502;

    private static final long serialVersionUID = 1L;

    public BadGatewayProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(DETAIL);
    }

}
