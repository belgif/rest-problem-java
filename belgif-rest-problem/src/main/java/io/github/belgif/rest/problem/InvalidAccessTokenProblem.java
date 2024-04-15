package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 401: Invalid Access Token (https://www.belgif.be/specification/rest/api-guide/problems/invalidAccessToken.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#invalid-access-token">
 *      https://www.belgif.be/specification/rest/api-guide/#invalid-access-token</a>
 */
@ProblemType(InvalidAccessTokenProblem.TYPE)
public class InvalidAccessTokenProblem extends ClientProblem {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:invalidAccessToken";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/invalidAccessToken.html");

    /**
     * The title.
     */
    public static final String TITLE = "Invalid Access Token";

    /**
     * The detail.
     */
    public static final String DETAIL = "The Bearer access token found in the Authorization HTTP header is invalid";

    /**
     * The status.
     */
    public static final int STATUS = 401;

    private static final long serialVersionUID = 1L;

    public InvalidAccessTokenProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(DETAIL);
    }

}
