package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 401: Expired Access Token (https://www.belgif.be/specification/rest/api-guide/problems/expiredAccessToken.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#expired-access-token">
 *      https://www.belgif.be/specification/rest/api-guide/#expired-access-token</a>
 */
@ProblemType(ExpiredAccessTokenProblem.TYPE)
public class ExpiredAccessTokenProblem extends ClientProblem implements HttpResponseHeaders {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:expiredAccessToken";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/expiredAccessToken.html");

    /**
     * The title.
     */
    public static final String TITLE = "Expired Access Token";

    /**
     * The detail.
     */
    public static final String DETAIL = "The Bearer access token found in the Authorization HTTP header has expired";

    /**
     * The status.
     */
    public static final int STATUS = 401;

    private static final long serialVersionUID = 1L;

    private String realm;

    public ExpiredAccessTokenProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(DETAIL);
    }

    public ExpiredAccessTokenProblem(String realm) {
        this();
        this.realm = realm;
    }

    public ExpiredAccessTokenProblem realm(String realm) {
        this.realm = realm;
        return this;
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE,
                "Bearer " + (realm != null ? "realm=\"" + realm + "\", " : "")
                        + "error=\"invalid_token\", error_description=\"The access token expired\"");
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
        ExpiredAccessTokenProblem that = (ExpiredAccessTokenProblem) o;
        return Objects.equals(realm, that.realm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), realm);
    }

}
