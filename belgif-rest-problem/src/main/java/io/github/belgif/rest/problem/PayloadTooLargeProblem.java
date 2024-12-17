package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.Objects;

import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.FluentProblem;
import io.github.belgif.rest.problem.api.ProblemType;

/**
 * HTTP 413: Payload Too Large (https://www.belgif.be/specification/rest/api-guide/problems/payloadTooLarge.html)
 *
 * @see <a href="https://www.belgif.be/specification/rest/api-guide/#payloadTooLargeProblem">
 *      https://www.belgif.be/specification/rest/api-guide/#payloadTooLargeProblem</a>
 */
@ProblemType(PayloadTooLargeProblem.TYPE)
public class PayloadTooLargeProblem extends ClientProblem implements FluentProblem<PayloadTooLargeProblem> {

    /**
     * The problem type.
     */
    public static final String TYPE = "urn:problem-type:belgif:payloadTooLarge";

    /**
     * The problem type URI.
     */
    public static final URI TYPE_URI = URI.create(TYPE);

    /**
     * The href URI.
     */
    public static final URI HREF =
            URI.create("https://www.belgif.be/specification/rest/api-guide/problems/payloadTooLarge.html");

    /**
     * The title.
     */
    public static final String TITLE = "Payload Too Large";

    /**
     * The status.
     */
    public static final int STATUS = 413;

    private static final long serialVersionUID = 1L;

    private Long limit;

    public PayloadTooLargeProblem() {
        super(TYPE_URI, HREF, TITLE, STATUS);
    }

    public PayloadTooLargeProblem(Long limit) {
        super(TYPE_URI, HREF, TITLE, STATUS);
        setLimit(limit);
    }

    public Long getLimit() {
        return limit;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
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
        PayloadTooLargeProblem that = (PayloadTooLargeProblem) o;
        return Objects.equals(limit, that.limit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), limit);
    }

}
