package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.List;

import io.github.belgif.rest.problem.api.FluentInputValidationProblem;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.InputValidationProblem;
import io.github.belgif.rest.problem.api.InvalidParam;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.i18n.I18N;

/**
 * HTTP 400: Bad Request (https://www.belgif.be/specification/rest/api-guide/problems/badRequest.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#bad-request">
 *      https://www.belgif.be/specification/rest/api-guide/#bad-request</a>
 * @see InputValidationProblem
 */
@ProblemType(BadRequestProblem.TYPE)
public class BadRequestProblem extends InputValidationProblem
        implements FluentInputValidationProblem<BadRequestProblem> {

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

    private static final long serialVersionUID = 1L;

    public BadRequestProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public BadRequestProblem(InputValidationIssue issue) {
        this();
        setDetail(I18N.getLocalizedDetail(BadRequestProblem.class));
        addIssue(issue);
    }

    public BadRequestProblem(List<InputValidationIssue> issues) {
        this();
        setDetail(I18N.getLocalizedDetail(BadRequestProblem.class));
        issues.forEach(this::addIssue);
    }

    /**
     * @deprecated use {@link #BadRequestProblem(InputValidationIssue)}
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
     * @deprecated use {@link #BadRequestProblem(InputValidationIssue)}
     */
    @Deprecated
    public BadRequestProblem(String detail, InvalidParam invalidParam) {
        this();
        setDetail(detail);
        addIssue(new InputValidationIssue(invalidParam.getIn(), invalidParam.getName(), invalidParam.getValue()));
    }

}
