package io.github.belgif.rest.problem;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class RestProblemSpringIT extends AbstractRestProblemIT {

    @LocalServerPort
    private int port;

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/frontend");
    }

    @Override
    protected Stream<String> getClients() {
        return Arrays.stream(Client.values()).map(Client::name);
    }

    @Override
    @Disabled("Not supported yet: https://github.com/belgif/rest-problem-java/issues/12")
    void beanValidation() {
        super.beanValidation();
    }

    @Test
    void pathParamInputViolation() {
        getSpec().when().get("/constraintViolationPath/1").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("path"))
                .body("issues.detail", hasItem("must be greater than or equal to 3"));
    }

    @Test
    void queryParamInputViolation() {
        getSpec().when().get("/constraintViolationQuery?id=100").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("query"))
                .body("issues.detail", hasItem("must be less than or equal to 10"));
    }

    @Test
    void headerParamInputViolation() {
        getSpec().when().header(new Header("id", "100")).get("/constraintViolationHeader").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("header"))
                .body("issues.detail", hasItem("must match \"^\\d\\d?$\""));
    }

    @Test
    void bodyInputViolation() {
        getSpec().when().body("{" +
                "\"email\": \"mymail.com\"" +
                "}")
                .contentType("application/json")
                .post("/methodArgumentNotValid").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("body"))
                .body("issues.detail", hasItem("must be a well-formed email address"))
                .body("issues.detail", hasItem("must not be blank"));
    }
}
