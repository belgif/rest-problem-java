package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 401: Invalid Access Token (https://www.belgif.be/specification/rest/api-guide/problems/invalidAccessToken.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#invalid-access-token">
 *      https://www.belgif.be/specification/rest/api-guide/#invalid-access-token</a>
 */
@ProblemType(InvalidAccessTokenProblem.TYPE)
public class InvalidAccessTokenProblem extends ClientProblem implements HttpResponseHeaders {

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

    private final String reason;

    private String realm;

    public InvalidAccessTokenProblem() {
        this(DETAIL, "The access token is invalid");
    }

    public InvalidAccessTokenProblem(String reason) {
        this(DETAIL + ": " + reason, reason);
    }

    private InvalidAccessTokenProblem(String detail, String reason) {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setDetail(detail);
        this.reason = reason;
    }

    public InvalidAccessTokenProblem realm(String realm) {
        this.realm = realm;
        return this;
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE,
                String.format("Bearer " + (realm != null ? "realm=\"" + realm + "\", " : "")
                        + "error=\"invalid_token\", error_description=\"%s\"", reason));
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
        InvalidAccessTokenProblem that = (InvalidAccessTokenProblem) o;
        return Objects.equals(reason, that.reason) && Objects.equals(realm, that.realm);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), reason, realm);
    }

}
