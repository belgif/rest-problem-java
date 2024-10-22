package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonSetter;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.HttpResponseHeaders;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 403: Missing Scope (https://www.belgif.be/specification/rest/api-guide/problems/missingScope.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#missing-scope">
 *      https://www.belgif.be/specification/rest/api-guide/#missing-scope</a>
 */
@ProblemType(MissingScopeProblem.TYPE)
public class MissingScopeProblem extends ClientProblem implements HttpResponseHeaders {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:missingScope";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/missingScope.html");

    /**
     * The title.
     */
    public static final String TITLE = "Missing Scope";

    /**
     * The status.
     */
    public static final int STATUS = 403;

    private static final long serialVersionUID = 1L;

    private final List<String> requiredScopes = new ArrayList<>();

    public MissingScopeProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public MissingScopeProblem(String... requiredScopes) {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setRequiredScopes(requiredScopes);
    }

    public List<String> getRequiredScopes() {
        return Collections.unmodifiableList(requiredScopes);
    }

    @JsonSetter
    public void setRequiredScopes(List<String> requiredScopes) {
        this.requiredScopes.clear();
        this.requiredScopes.addAll(requiredScopes);
    }

    public void setRequiredScopes(String... requiredScopes) {
        this.requiredScopes.clear();
        this.requiredScopes.addAll(Arrays.asList(requiredScopes));
    }

    public void addRequiredScope(String requiredScope) {
        requiredScopes.add(requiredScope);
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
        MissingScopeProblem that = (MissingScopeProblem) o;
        return Objects.equals(requiredScopes, that.requiredScopes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), requiredScopes);
    }

    @Override
    public Map<String, Object> getHttpResponseHeaders() {
        return Collections.singletonMap(WWW_AUTHENTICATE, "Bearer error=\"insufficient_scope\"");
    }

}
