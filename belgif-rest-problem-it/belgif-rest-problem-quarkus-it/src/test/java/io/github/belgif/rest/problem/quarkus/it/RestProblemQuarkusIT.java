package io.github.belgif.rest.problem.quarkus.it;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.AbstractRestProblemIT;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
class RestProblemQuarkusIT extends AbstractRestProblemIT {

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost")
                .port(8081)
                .basePath("/quarkus/frontend");
    }

    @Test
    void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405);
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }
}
