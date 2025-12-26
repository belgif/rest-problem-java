package io.github.belgif.rest.problem.quarkus.it;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.it.AbstractRestProblemEEIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestHTTPEndpoint(Frontend.class)
class RestProblemQuarkusIT extends AbstractRestProblemEEIT {

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

    @Test
    @Override
    @Disabled("EJB not supported on Quarkus")
    public void ejb() {
    }

    @Test
    @Override
    @Disabled("Does not work on Quarkus (yet)")
    public void invalidJsonEOF() {
    }

    @Test
    @Override
    @Disabled("Does not work on Quarkus (yet)")
    public void invalidJson() {
    }

    @Test
    @Override
    @Disabled("Does not work on Quarkus (yet)")
    public void invalidJsonFormat() {
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }
}
