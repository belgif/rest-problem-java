package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.FluentProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 401: No Access Token (https://www.belgif.be/specification/rest/api-guide/problems/noAccessToken.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#no-access-token">
 *      https://www.belgif.be/specification/rest/api-guide/#no-access-token</a>
 */
@ProblemType(NoAccessTokenProblem.TYPE)
public class NoAccessTokenProblem extends ClientProblem
        implements FluentProblem<NoAccessTokenProblem>, HttpResponseHeaders {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:noAccessToken";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/noAccessToken.html");

    /**
     * The title.
     */
    public static final String TITLE = "No Access Token";

    /**
     * The detail.
     */
    public static final String DETAIL = "No Bearer access token found in Authorization HTTP header";

    /**
     * The status.
     */
    public static final int STATUS = 401;

    private static final long serialVersionUID = 1L;

    public NoAccessTokenProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(DETAIL);
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE, "Bearer");
    }

}
