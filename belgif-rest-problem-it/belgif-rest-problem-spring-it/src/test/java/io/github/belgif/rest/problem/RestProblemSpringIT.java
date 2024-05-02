package io.github.belgif.rest.problem;

import java.util.Arrays;
import java.util.stream.Stream;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import io.restassured.RestAssured;
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

}
