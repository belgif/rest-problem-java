package io.github.belgif.rest.problem;

import static org.hamcrest.Matchers.*;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
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

    // beanValidation in superclass won't work this way with the Spring framework.
    // Spring cannot find a method with path /beanValidation and only a 'positive' queryParam, so it throws a
    // MissingServletRequestParameterException
    // The 'positive' queryParam isn't validated by Spring, because it throws the exception before the validation phase.
    @Override
    @Test
    void beanValidation() {
        getSpec().when().queryParam("positive", -1)
                .get("/beanValidation").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues[0].type", equalTo("urn:problem-type:belgif:input-validation:schemaViolation"))
                .body("issues[0].title", equalTo("Input value is invalid with respect to the schema"))
                .body("issues[0].detail", equalTo("Required parameter 'required' is not present."))
                .body("issues[0].in", equalTo("query"))
                .body("issues[0].name", equalTo("required"))
                .body("issues[0].value", nullValue());
    }

    // Since Spring throws different exceptions for different kind of validation issues
    // Several ITs are implemented on different types of validations.
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

    @Test
    void nestedQueryParamsViolation() {
        getSpec().when()
                .contentType("application/json")
                .post("/nestedQueryParams?email=myemail.com&name=myName").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("query"))
                .body("issues.detail", hasItem("must be a well-formed email address"));
    }

    @Test
    void doubleNestedQueryParamsViolation() {
        getSpec().when()
                .contentType("application/json")
                .post("/nestedQueryParams?email=myemail.com").then().assertThat()
                .statusCode(400)
                .body("type", equalTo("urn:problem-type:belgif:badRequest"))
                .body("issues.in", hasItem("query"))
                .body("issues.detail", hasItem("must be a well-formed email address"))
                .body("issues.detail", hasItem("must not be blank"));
    }
}
