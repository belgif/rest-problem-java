package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.Test;

abstract class AbstractRestProblemEEIT extends AbstractRestProblemIT {

    @Test
    void ejb() {
        getSpec().when()
                .get("/ejb").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("detail", equalTo("problem from EJB"));
    }

}
