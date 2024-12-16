package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.FluentInputValidationProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationProblem;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 403: Missing Permission (https://www.belgif.be/specification/rest/api-guide/problems/missingPermission.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#missing-permission">
 *      https://www.belgif.be/specification/rest/api-guide/#missing-permission</a>
 */
@ProblemType(MissingPermissionProblem.TYPE)
public class MissingPermissionProblem extends InputValidationProblem
        implements FluentInputValidationProblem<MissingPermissionProblem> {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:missingPermission";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/missingPermission.html");

    /**
     * The title.
     */
    public static final String TITLE = "Missing Permission";

    /**
     * The status.
     */
    public static final int STATUS = 403;

    private static final long serialVersionUID = 1L;

    public MissingPermissionProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public MissingPermissionProblem(String detail, InputValidationIssue... issues) {
        this();
        setDetail(detail);
        if (issues != null) {
            for (InputValidationIssue issue : issues) {
                addIssue(issue);
            }
        }
    }

}
