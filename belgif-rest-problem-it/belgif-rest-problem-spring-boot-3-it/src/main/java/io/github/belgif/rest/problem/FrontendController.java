package io.github.belgif.rest.problem;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.model.ChildModel;
import io.github.belgif.rest.problem.model.Model;
import io.github.belgif.rest.problem.model.NestedModel;

@RestController
@RequestMapping("/frontend")
@Validated
public class FrontendController implements ControllerInterface {

    private static final String DETAIL_MESSAGE_SUFFIX = " (caught successfully by frontend)";
    private static final String ILLEGAL_STATE_MESSAGE_PREFIX = "Unsupported client ";

    private final RestTemplateBuilder restTemplateBuilder;

    private final WebClient.Builder webClientBuilder;

    private final RestClient.Builder restClientBuilder;

    private RestTemplate restTemplate;

    private WebClient webClient;

    private RestClient restClient;

    public FrontendController(RestTemplateBuilder restTemplateBuilder,
            WebClient.Builder webClientBuilder,
            RestClient.Builder restClientBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.webClientBuilder = webClientBuilder;
        this.restClientBuilder = restClientBuilder;
    }

    @EventListener
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        this.restTemplate = restTemplateBuilder
                .rootUri("http://localhost:" + event.getWebServer().getPort() + "/spring/backend")
                .build();
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:" + event.getWebServer().getPort() + "/spring/backend")
                .build();
        this.restClient = restClientBuilder
                .baseUrl("http://localhost:" + event.getWebServer().getPort() + "/spring/backend")
                .build();
    }

    @GetMapping("/badRequest")
    public void badRequest() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request from frontend");
        throw problem;
    }

    @GetMapping("/custom")
    public void custom() {
        throw new CustomProblem("value from frontend");
    }

    @GetMapping("/runtime")
    public void runtime() {
        throw new RuntimeException("oops");
    }

    @GetMapping("/unmapped")
    public void unmapped() {
        Problem unmapped = new Problem(URI.create("urn:problem-type:belgif:test:unmapped"), "Unmapped problem", 400) {
        };
        unmapped.setDetail("Unmapped problem from frontend");
        throw unmapped;
    }

    @GetMapping("/retryAfter")
    public void retryAfter() {
        ServiceUnavailableProblem problem = new ServiceUnavailableProblem();
        problem.setRetryAfterSec(10000L);
        throw problem;
    }

    @GetMapping("/badRequestFromBackend")
    public void badRequestFromBackend(@RequestParam("client") Client client) {
        try {
            if (client == Client.REST_TEMPLATE) {
                restTemplate.getForObject("/badRequest", String.class);
            } else if (client == Client.WEB_CLIENT) {
                webClient.get().uri("/badRequest").retrieve().toEntity(String.class).block();
            } else if (client == Client.REST_CLIENT) {
                restClient.get().uri("/badRequest").retrieve().toEntity(String.class);
            }
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
        } catch (BadRequestProblem e) {
            e.setDetail(e.getDetail() + DETAIL_MESSAGE_SUFFIX);
            throw e;
        }
    }

    @GetMapping("/customFromBackend")
    public void customFromBackend(@RequestParam("client") Client client) {
        try {
            if (client == Client.REST_TEMPLATE) {
                restTemplate.getForObject("/custom", String.class);
            } else if (client == Client.WEB_CLIENT) {
                webClient.get().uri("/custom").retrieve().toEntity(String.class).block();
            } else if (client == Client.REST_CLIENT) {
                restClient.get().uri("/custom").retrieve().toEntity(String.class);
            }
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
        } catch (CustomProblem e) {
            e.setCustomField(e.getCustomField() + DETAIL_MESSAGE_SUFFIX);
            throw e;
        }
    }

    @GetMapping("/unmappedFromBackend")
    public void unmappedFromBackend(@RequestParam("client") Client client) {
        try {
            if (client == Client.REST_TEMPLATE) {
                restTemplate.getForObject("/unmapped", String.class);
            } else if (client == Client.WEB_CLIENT) {
                webClient.get().uri("/unmapped").retrieve().toEntity(String.class).block();
            } else if (client == Client.REST_CLIENT) {
                restClient.get().uri("/unmapped").retrieve().toEntity(String.class);
            }
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
        } catch (DefaultProblem e) {
            e.setDetail(e.getDetail() + DETAIL_MESSAGE_SUFFIX);
            throw e;
        }
    }

    @GetMapping("/beanValidation/queryParameter")
    public ResponseEntity<String> beanValidationQueryParameter(
            @RequestParam("param") @Positive @NotNull Integer param,
            @RequestParam("other") @Size(max = 5) String other) {
        return ResponseEntity.ok("param: " + param + ", other: " + other);
    }

    @GetMapping("/beanValidation/headerParameter")
    public ResponseEntity<String> beanValidationHeaderParameter(
            @RequestHeader("param") @Positive @NotNull Integer param) {
        return ResponseEntity.ok("param: " + param);
    }

    @GetMapping("/beanValidation/pathParameter/class/{param}")
    public ResponseEntity<String> beanValidationPathParameter(
            @PathVariable("param") @Positive @NotNull Integer param) {
        return ResponseEntity.ok("param: " + param);
    }

    @Override
    public ResponseEntity<String> beanValidationPathParameterInherited(Integer param) {
        return ResponseEntity.ok("param: " + param);
    }

    @Override
    @GetMapping("/beanValidation/pathParameter/overridden")
    public ResponseEntity<String> beanValidationPathParameterOverridden(@RequestParam Integer param) {
        return ResponseEntity.ok("param: " + param);
    }

    @PostMapping("/beanValidation/body")
    public ResponseEntity<String> beanValidationBody(@Valid @RequestBody Model body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostMapping("/beanValidation/body/nested")
    public ResponseEntity<String> beanValidationBodyNested(@Valid @RequestBody NestedModel body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostMapping("/beanValidation/body/inheritance")
    public ResponseEntity<String> beanValidationBodyInheritance(@Valid @RequestBody ChildModel body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostMapping("/beanValidation/queryParameter/nested")
    public ResponseEntity<String> beanValidationQueryParameterNested(@Valid Model param) {
        return ResponseEntity.ok("param: " + param);
    }

}
