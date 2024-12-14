package io.github.belgif.rest.problem.quarkus.it;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.AbstractRestProblemIT;
import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@QuarkusTest
@TestHTTPEndpoint(Frontend.class)
class RestProblemQuarkusIT extends AbstractRestProblemIT {

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.given();
    }

    @Test
    void methodNotAllowed() {
        getSpec().when().post("/custom").then().assertThat()
                .statusCode(405);
    }

    @Test
    @Disabled("I18N not currently supported on Quarkus")
    void i18n() {
    }

    @Test
    @Disabled("I18N not currently supported on Quarkus")
    void i18nWeighted() {
    }

    @Test
    @Disabled("I18N not currently supported on Quarkus")
    void i18nCustom() {
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }
}
