package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.i18n.I18N;

/**
 * HTTP 401: No Access Token (https://www.belgif.be/specification/rest/api-guide/problems/noAccessToken.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#no-access-token">
 *      https://www.belgif.be/specification/rest/api-guide/#no-access-token</a>
 */
@ProblemType(NoAccessTokenProblem.TYPE)
public class NoAccessTokenProblem extends ClientProblem implements HttpResponseHeaders {

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
     * The status.
     */
    public static final int STATUS = 401;

    private static final long serialVersionUID = 1L;

    public NoAccessTokenProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(I18N.getLocalizedDetail(NoAccessTokenProblem.class));
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE, "Bearer");
    }

}
