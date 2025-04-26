package io.github.belgif.rest.problem.quarkus.it;

import io.github.belgif.rest.problem.it.AbstractRestProblemExtIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.QuarkusTestProfile;
import io.quarkus.test.junit.TestProfile;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestProfile(RestProblemQuarkusExtIT.class)
@TestHTTPEndpoint(Frontend.class)
public class RestProblemQuarkusExtIT extends AbstractRestProblemExtIT implements QuarkusTestProfile {

    @Override
    public String getConfigProfile() {
        return "ext";
    }

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.given();
    }

}
