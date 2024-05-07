package io.github.belgif.rest.problem;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.model.ChildRequestBody;
import io.github.belgif.rest.problem.model.NestedRequestBody;
import io.github.belgif.rest.problem.model.ParentRequestBody;

@RestController
@RequestMapping("/frontend")
@Validated
public class FrontendController implements ControllerInterface {

    private final RestTemplateBuilder restTemplateBuilder;

    private final WebClient.Builder webClientBuilder;

    private RestTemplate restTemplate;

    private WebClient webClient;

    private static final String DETAIL_MESSAGE_SUFFIX = " (caught successfully by frontend)";
    private static final String ILLEGAL_STATE_MESSAGE_PREFIX = "Unsupported client ";

    public FrontendController(RestTemplateBuilder restTemplateBuilder, WebClient.Builder webClientBuilder) {
        this.restTemplateBuilder = restTemplateBuilder;
        this.webClientBuilder = webClientBuilder;
    }

    @EventListener
    public void onApplicationEvent(ServletWebServerInitializedEvent event) {
        this.restTemplate = restTemplateBuilder
                .rootUri("http://localhost:" + event.getWebServer().getPort() + "/spring/backend")
                .build();
        this.webClient = webClientBuilder
                .baseUrl("http://localhost:" + event.getWebServer().getPort() + "/spring/backend").build();
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
            }
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
        } catch (DefaultProblem e) {
            e.setDetail(e.getDetail() + DETAIL_MESSAGE_SUFFIX);
            throw e;
        }
    }

    @GetMapping("/beanValidation")
    public ResponseEntity<String> beanValidation(@Valid @RequestParam("positive") @Min(0) int positive,
            @Valid @RequestParam("required") @NotBlank String required) {
        return ResponseEntity.ok("Positive: " + positive + "\nRequired: " + required);
    }

    // TODO
//    @GetMapping("/constraintViolationPath/{id}")
//    public ResponseEntity<String> constraintViolationPath(@Valid @PathVariable("id") @Min(3) @Max(10) int id) {
//        return ResponseEntity.ok("" + id);
//    }

    @Override
    public ResponseEntity<String> constraintViolationPath(int id) {
        return ResponseEntity.ok("" + id);
    }

    @GetMapping("/constraintViolationQuery")
    public ResponseEntity<String> constraintViolationQuery(@Valid @RequestParam("id") @Min(3) @Max(10) int id) {
        return ResponseEntity.ok("" + id);
    }

    @GetMapping("/constraintViolationHeader")
    public ResponseEntity<String>
            constraintViolationHeader(@Valid @RequestHeader("id") @Min(3) @Max(10) int id) {
        return ResponseEntity.ok("" + id);
    }

    @PostMapping("/methodArgumentNotValid")
    public ResponseEntity<String> methodArgumentNotValidBody(@Valid @RequestBody ParentRequestBody body) {
        return ResponseEntity.ok("Email: " + body.getEmail());
    }

    @PostMapping("/nestedQueryParams")
    public ResponseEntity<String> methodArgumentNotValidBodyNoAnnotation(@Valid ParentRequestBody nestedQueryParams) {
        return ResponseEntity.ok("Email: " + nestedQueryParams.getEmail());
    }

    @PostMapping("/superClassValidation")
    public ResponseEntity<String> superClassValidation(@Valid @RequestBody ChildRequestBody body) {
        return ResponseEntity.ok("Email: " + body.getEmail());
    }

    @PostMapping("/nestedValidation")
    public ResponseEntity<String> nestedValidation(@Valid @RequestBody NestedRequestBody body) {
        return ResponseEntity.ok("Email: " + body.getMyRequestBody().getEmail());
    }

}
