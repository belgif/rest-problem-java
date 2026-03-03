package io.github.belgif.rest.problem.it;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
// uses spring boot to start the server-side, but doesn't rely on starter integration with rest-problem
class RestProblemSpringIT extends AbstractRestProblemSpringBootIT {

    @LocalServerPort
    private int port;

    @Autowired
    private FrontendController frontendController;

    @BeforeEach
    public void init() {
        frontendController.initClients(port);
    }

    @Override
    protected RequestSpecification getSpec() {
        return RestAssured.with().baseUri("http://localhost").port(port).basePath("/spring/frontend");
    }

    @Override
    protected Stream<String> getClients() {
        return List.of(Client.REST_CLIENT.name(), Client.WEB_CLIENT.name()).stream();
    }
}
