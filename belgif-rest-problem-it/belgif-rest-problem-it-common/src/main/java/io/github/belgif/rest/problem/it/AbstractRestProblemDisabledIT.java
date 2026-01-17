package io.github.belgif.rest.problem.it;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.TestInstance.Lifecycle.*;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import io.github.belgif.rest.problem.config.ProblemConfig;
import io.restassured.specification.RequestSpecification;

@TestInstance(PER_CLASS)
// PER_CLASS because otherwise @MethodSource("getClients") requires a static getClients() method
public abstract class AbstractRestProblemDisabledIT {

    protected abstract RequestSpecification getSpec();

    @AfterAll
    static void afterAll() {
        ProblemConfig.reset();
    }

    @Test
    public void ping() {
        getSpec().when().get("/ping").then().assertThat()
                .statusCode(200)
                .body(is("pong"));
    }

    @Test
    public void badRequest() {
        getSpec().when().get("/badRequest").then().log().all().assertThat()
                .statusCode(500)
                .body(not(containsString("urn:problem-type:belgif:badRequest")));
    }

    @Test
    public void runtime() {
        getSpec().when().get("/runtime").then().log().all().assertThat()
                .statusCode(500)
                .body(not(containsString("urn:problem-type:belgif:internalServerError")));
    }

}
