package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

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
     * The detail.
     */
    public static final String DETAIL = "No Bearer access token found in Authorization HTTP header";

    /**
     * The status.
     */
    public static final int STATUS = 401;

    private static final long serialVersionUID = 1L;

    private String realm;

    public NoAccessTokenProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(DETAIL);
    }

    public NoAccessTokenProblem(String realm) {
        this();
        this.realm = realm;
    }

    public NoAccessTokenProblem realm(String realm) {
        this.realm = realm;
        return this;
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE,
                "Bearer" + (realm != null ? " realm=\"" + realm + "\"" : ""));
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
        NoAccessTokenProblem that = (NoAccessTokenProblem) o;
        return Objects.equals(realm, that.realm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), realm);
    }

}
