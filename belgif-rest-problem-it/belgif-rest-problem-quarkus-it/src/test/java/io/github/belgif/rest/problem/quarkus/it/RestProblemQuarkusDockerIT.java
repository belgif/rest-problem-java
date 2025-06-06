package io.github.belgif.rest.problem.quarkus.it;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.it.AbstractRestProblemIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusIntegrationTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@TestHTTPEndpoint(Frontend.class)
@QuarkusIntegrationTest
class RestProblemQuarkusDockerIT extends AbstractRestProblemIT {

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.given();
    }

    @Test
    @Override
    public void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405);
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }
}
