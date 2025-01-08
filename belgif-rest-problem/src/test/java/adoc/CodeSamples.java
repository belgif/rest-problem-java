package adoc;

import java.net.URI;
import java.util.List;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.ResourceNotFoundProblem;
import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.FluentProblem;
import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;

public class CodeSamples {

    // tag::api-local-problem[]
    @ProblemType(TooManyResultsProblem.TYPE)
    public static class TooManyResultsProblem extends ClientProblem
            implements FluentProblem<TooManyResultsProblem> {

        public static final String TYPE = "urn:problem-type:cbss:legallog:tooManyResults";
        public static final URI TYPE_URI = URI.create(TYPE);
        public static final URI HREF =
                URI.create("https://api.ksz-bcss.fgov.be/legallog/v1/refData/problemTypes/" + TYPE);
        public static final String TITLE = "Too Many Results";
        public static final int STATUS = 400;

        private static final long serialVersionUID = 1L;

        public TooManyResultsProblem() {
            super(TYPE_URI, HREF, TITLE, STATUS);
            setDetail("Result's number of global transactions exceeds the limit");
        }

        public TooManyResultsProblem(int limit) {
            this();
            setDetail("Result's number of global transactions exceeds the limit of " + limit);
        }

    }
    // end::api-local-problem[]

    public void throwProblem() {
        String sectorCode = "";
        // tag::throw-problem[]
        throw new BadRequestProblem(
                new InputValidationIssue(InEnum.PATH, "sectorCode", sectorCode)
                        .title("Invalid sector code"));
        // end::throw-problem[]
    }

    public void catchProblem() {
        class API {
            void someOperation() {
            }
        }
        API api = new API();
        // tag::catch-problem[]
        try {
            api.someOperation();
        } catch (BadRequestProblem p) {
            String detail = p.getDetail();
            List<InputValidationIssue> issues = p.getIssues();
            // handle the BadRequestProblem
        } catch (ResourceNotFoundProblem p) {
            String detail = p.getDetail();
            List<InputValidationIssue> issues = p.getIssues();
            // handle the ResourceNotFoundProblem
        } catch (Problem e) {
            // handle all other problems
        }
        // end::catch-problem[]
    }

}
