package adoc;

import java.time.LocalDate;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.api.InEnum;
import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.InputValidationIssue;
import io.github.belgif.rest.problem.validation.RequestValidator;

public class CodeSamples {

    private static final Logger LOGGER = LoggerFactory.getLogger(CodeSamples.class);

    public void requestValidator() {
        String ssin = "11111111111";
        String enterpriseNumber = "2222222222";
        LocalDate startDate = LocalDate.parse("2023-12-31");
        LocalDate endDate = LocalDate.parse("2023-01-01");
        String password = "oops";

        // tag::request-validator[]
        // example for an API resource with query parameters:
        // ssin, enterpriseNumber, startDate, endDate and password
        new RequestValidator()
                .ssin(Input.query("ssin", ssin)) // <1>
                .enterpriseNumber(Input.query("enterpriseNumber", enterpriseNumber)) // <2>
                .period(Input.query("startDate", startDate), Input.query("endDate", endDate)) // <3>
                .exactlyOneOf(Input.query("ssin", ssin),
                        Input.query("enterpriseNumber", enterpriseNumber)) // <4>
                .custom(() -> { // <5>
                    // custom validation logic returning Optional<InputValidationIssue>
                    if (!"secret".equals(password)) {
                        return Optional.of(new InputValidationIssue(InEnum.QUERY, "password", password)
                                .type("urn:problem-type:cbss:input-validation:example:invalidPassword")
                                .title("Invalid password"));
                    }
                    return Optional.empty();
                })
                .validate(); // <6>
        // end::request-validator[]
    }

}
