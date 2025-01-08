package io.github.belgif.rest.problem;

import java.net.URI;

import io.github.belgif.rest.problem.api.FluentProblem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.api.ServerProblem;

/**
 * HTTP 500: Internal Server Error
 * (https://www.belgif.be/specification/rest/api-guide/problems/internalServerError.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#internal-server-error">
 *      https://www.belgif.be/specification/rest/api-guide/#internal-server-error</a>
 */
@ProblemType(InternalServerErrorProblem.TYPE)
public class InternalServerErrorProblem extends ServerProblem implements FluentProblem<InternalServerErrorProblem> {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:internalServerError";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/internalServerError.html");

    /**
     * The title.
     */
    public static final String TITLE = "Internal Server Error";

    /**
     * The status.
     */
    public static final int STATUS = 500;

    private static final long serialVersionUID = 1L;

    public InternalServerErrorProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

}
