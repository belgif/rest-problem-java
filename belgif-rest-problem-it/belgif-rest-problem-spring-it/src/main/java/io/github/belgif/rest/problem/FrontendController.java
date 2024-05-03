package io.github.belgif.rest.problem;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

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
import io.github.belgif.rest.problem.model.MyRequestBody;

@RestController
@RequestMapping("/frontend")
@Validated
public class FrontendController {

    private final RestTemplateBuilder restTemplateBuilder;

    private final WebClient.Builder webClientBuilder;

    private RestTemplate restTemplate;

    private WebClient webClient;

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
            throw new IllegalStateException("Unsupported client " + client);
        } catch (BadRequestProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
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
            throw new IllegalStateException("Unsupported client " + client);
        } catch (CustomProblem e) {
            e.setCustomField(e.getCustomField() + " (caught successfully by frontend)");
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
            throw new IllegalStateException("Unsupported client " + client);
        } catch (DefaultProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @GetMapping("/beanValidation")
    public ResponseEntity<String> beanValidation(@Valid @RequestParam("positive") @Min(0) int positive,
            @Valid @RequestParam("required") @NotBlank String required) {
        return ResponseEntity.ok("Positive: " + positive + "\nRequired: " + required);
    }

    @GetMapping("/constraintViolationPath/{id}")
    public ResponseEntity<String> constraintViolationPath(@Valid @PathVariable("id") @Min(3) @Max(10) int id) {
        return ResponseEntity.ok(id + " was the ID");
    }

    @GetMapping("/constraintViolationQuery")
    public ResponseEntity<String> constraintViolationQuery(@Valid @RequestParam("id") @Min(3) @Max(10) int id) {
        return ResponseEntity.ok(id + " was the ID");
    }

    @GetMapping("/constraintViolationHeader")
    public ResponseEntity<String>
            constraintViolationHeader(@Valid @RequestHeader("id") @Pattern(regexp = "^\\d\\d?$") String id) {
        return ResponseEntity.ok(id + " was the ID");
    }

    @PostMapping("/methodArgumentNotValid")
    public ResponseEntity<String> methodArgumentNotValidBody(@Valid @RequestBody MyRequestBody body) {
        return ResponseEntity.ok(body.getEmail() + " was the Email");
    }

}
