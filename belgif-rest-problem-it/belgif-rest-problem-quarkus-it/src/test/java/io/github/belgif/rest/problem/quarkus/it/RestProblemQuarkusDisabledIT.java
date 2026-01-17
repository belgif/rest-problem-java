package io.github.belgif.rest.problem.quarkus.it;

import io.github.belgif.rest.problem.it.AbstractRestProblemDisabledIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestProfile(RestProblemQuarkusDisabledIT.class)
@TestHTTPEndpoint(Frontend.class)
public class RestProblemQuarkusDisabledIT extends AbstractRestProblemDisabledIT implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "disabled";
    }

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.given();
    }

}
