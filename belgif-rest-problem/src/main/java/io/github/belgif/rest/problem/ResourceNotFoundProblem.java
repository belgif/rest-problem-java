package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationProblem;
import io.github.belgif.rest.problem.api.InvalidParam;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 404: Resource Not Found (https://www.belgif.be/specification/rest/api-guide/problems/resourceNotFound.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#resource-not-found">
 *      https://www.belgif.be/specification/rest/api-guide/#resource-not-found</a>
 * @see InputValidationProblem
 */
@ProblemType(ResourceNotFoundProblem.TYPE)
public class ResourceNotFoundProblem extends InputValidationProblem {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:resourceNotFound";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/resourceNotFound.html");

    /**
     * The title.
     */
    public static final String TITLE = "Resource Not Found";

    /**
     * The status.
     */
    public static final int STATUS = 404;

    private static final long serialVersionUID = 1L;

    /**
     * Deprecated invalid params, for backwards compatibility with supplier services using legacy problem type.
     */
    @Deprecated
    private final List<InvalidParam> invalidParams = new ArrayList<>();

    public ResourceNotFoundProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    /**
     * Deprecated ResourceNotFoundProblem constructor with InvalidParam.
     *
     * @param resource the resource name
     * @param param the resource key
     * @deprecated replaced by {@link #ResourceNotFoundProblem(String, InEnum, String, Object)}
     */
    @Deprecated
    public ResourceNotFoundProblem(String resource, InvalidParam param) {
        this(resource, param.getIn(), param.getName(), param.getValue());
    }

    public ResourceNotFoundProblem(String resource, InEnum in, String name, Object value) {
        this("No data found for the " + resource + " with " + name + " [" + value + "]",
                new InputValidationIssue(in, name, value));
    }

    public ResourceNotFoundProblem(String detail, InputValidationIssue issue) {
        this();
        setDetail(detail);
        addIssue(issue);
    }

    @Deprecated
    public List<InvalidParam> getInvalidParams() {
        return Collections.unmodifiableList(invalidParams);
    }

    @Deprecated
    public void setInvalidParams(List<InvalidParam> invalidParams) {
        this.invalidParams.clear();
        this.invalidParams.addAll(invalidParams);
    }

}
