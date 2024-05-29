package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.List;

import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationProblem;
import io.github.belgif.rest.problem.api.InvalidParam;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 400: Bad Request (https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#bad-request">
 *      https://www.belgif.be/specification/rest/api-guide/#bad-request</a>
 * @see InputValidationProblem
 */
@ProblemType(BadRequestProblem.TYPE)
public class BadRequestProblem extends InputValidationProblem {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:badRequest";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html");

    /**
     * The title.
     */
    public static final String TITLE = "Bad Request";

    /**
     * The status.
     */
    public static final int STATUS = 400;

    /**
     * The default detail message.
     */
    public static final String DEFAULT_DETAIL = "The input message is incorrect";

    private static final long serialVersionUID = 1L;

    public BadRequestProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public BadRequestProblem(InputValidationIssue issue) {
        this();
        setDetail(DEFAULT_DETAIL);
        addIssue(issue);
    }

    public BadRequestProblem(List<InputValidationIssue> issues) {
        this();
        setDetail(DEFAULT_DETAIL);
        issues.forEach(this::addIssue);
    }

    /**
     * Deprecated ResourceNotFoundProblem constructor with InvalidParam.
     *
     * @param invalidParam the invalid param
     * @deprecated replaced by {@link #BadRequestProblem(InputValidationIssue)}
     */
    @Deprecated
    public BadRequestProblem(InvalidParam invalidParam) {
        this();
        String detail = "Invalid parameter '" + invalidParam.getName() + "'";
        if (invalidParam.getValue() != null) {
            detail += " [" + invalidParam.getValue() + "]";
        }
        setDetail(detail);
        addIssue(new InputValidationIssue(invalidParam.getIn(), invalidParam.getName(), invalidParam.getValue()));
    }

    /**
     * Deprecated ResourceNotFoundProblem constructor with InvalidParam.
     *
     * @param detail the detail message
     * @param invalidParam the invalid param
     * @deprecated replaced by {@link #BadRequestProblem(InputValidationIssue)}
     */
    @Deprecated
    public BadRequestProblem(String detail, InvalidParam invalidParam) {
        this();
        setDetail(detail);
        addIssue(new InputValidationIssue(invalidParam.getIn(), invalidParam.getName(), invalidParam.getValue()));
    }

}
