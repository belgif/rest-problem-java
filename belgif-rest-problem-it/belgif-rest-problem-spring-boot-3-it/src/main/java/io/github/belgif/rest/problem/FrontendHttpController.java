package io.github.belgif.rest.problem;

import java.net.URI;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.servlet.context.ServletWebServerInitializedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Input;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.it.model.ChildModel;
import io.github.belgif.rest.problem.it.model.JacksonModel;
import io.github.belgif.rest.problem.it.model.Model;
import io.github.belgif.rest.problem.it.model.NestedModel;
import io.github.belgif.rest.problem.validation.RequestValidator;

@RestController
@HttpExchange("/frontend-http")
@Validated
public class FrontendHttpController implements ControllerHttpInterface {

    private static final String DETAIL_MESSAGE_SUFFIX = " (caught successfully by frontend)";
    private static final String ILLEGAL_STATE_MESSAGE_PREFIX = "Unsupported client ";

    private final RestTemplateBuilder restTemplateBuilder;

    private final WebClient.Builder webClientBuilder;

    private final RestClient.Builder restClientBuilder;

    private RestTemplate restTemplate;

    private WebClient webClient;

    private RestClient restClient;

    public FrontendHttpController(RestTemplateBuilder restTemplateBuilder,
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

    @GetExchange("/ping")
    public String ping() {
        return "pong";
    }

    @GetExchange("/badRequest")
    public void badRequest() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request from frontend");
        throw problem;
    }

    @GetExchange("/custom")
    public void custom() {
        throw new CustomProblem("value from frontend");
    }

    @GetExchange("/runtime")
    public void runtime() {
        throw new RuntimeException("oops");
    }

    @GetExchange("/unmapped")
    public void unmapped() {
        Problem unmapped = new Problem(URI.create("urn:problem-type:belgif:test:unmapped"), "Unmapped problem", 400) {
        };
        unmapped.setDetail("Unmapped problem from frontend");
        throw unmapped;
    }

    @GetExchange("/retryAfter")
    public void retryAfter() {
        ServiceUnavailableProblem problem = new ServiceUnavailableProblem();
        problem.setRetryAfterSec(10000L);
        throw problem;
    }

    @GetExchange(value = "/okFromBackend", accept = "application/json")
    public ResponseEntity<String> okFromBackend(@RequestParam("client") Client client) {
        String result = null;
        if (client == Client.REST_TEMPLATE) {
            result = restTemplate.getForObject("/ok", String.class);
        } else if (client == Client.WEB_CLIENT) {
            result = webClient.get().uri("/ok").retrieve().toEntity(String.class).block().getBody();
        } else if (client == Client.REST_CLIENT) {
            result = restClient.get().uri("/ok").retrieve().toEntity(String.class).getBody();
        }
        return ResponseEntity.ok(result);
    }

    @GetExchange("/badRequestFromBackend")
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

    @GetExchange("/customFromBackend")
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

    @GetExchange("/unmappedFromBackend")
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

    @GetExchange("/applicationJsonProblemFromBackend")
    public void applicationJsonProblemFromBackend(@RequestParam("client") Client client) {
        try {
            if (client == Client.REST_TEMPLATE) {
                restTemplate.getForObject("/applicationJsonProblem", String.class);
            } else if (client == Client.WEB_CLIENT) {
                webClient.get().uri("/applicationJsonProblem").retrieve().toEntity(String.class).block();
            } else if (client == Client.REST_CLIENT) {
                restClient.get().uri("/applicationJsonProblem").retrieve().toEntity(String.class);
            }
            throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
        } catch (BadRequestProblem e) {
            e.setDetail(e.getDetail() + DETAIL_MESSAGE_SUFFIX);
            throw e;
        }
    }

    @GetExchange("/jacksonMismatchedInputFromBackend")
    public void jacksonMismatchedInputFromBackend(@RequestParam("client") Client client) {
        if (client == Client.REST_TEMPLATE) {
            restTemplate.getForObject("/jacksonMismatchedInput", JacksonModel.class);
        } else if (client == Client.WEB_CLIENT) {
            webClient.get().uri("/jacksonMismatchedInput").retrieve().toEntity(JacksonModel.class).block();
        } else if (client == Client.REST_CLIENT) {
            restClient.get().uri("/jacksonMismatchedInput").retrieve().toEntity(JacksonModel.class);
        }
        throw new IllegalStateException(ILLEGAL_STATE_MESSAGE_PREFIX + client);
    }

    @GetExchange("/beanValidation/queryParameter")
    public ResponseEntity<String> beanValidationQueryParameter(
            @RequestParam("param") @Positive @NotNull Integer p,
            @RequestParam @Size(max = 5) String other) {
        return ResponseEntity.ok("param: " + p + ", other: " + other);
    }

    @GetExchange("/beanValidation/headerParameter")
    public ResponseEntity<String> beanValidationHeaderParameter(
            @RequestHeader("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @GetExchange("/beanValidation/pathParameter/class/{param}")
    public ResponseEntity<String> beanValidationPathParameter(
            @PathVariable("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @Override
    public ResponseEntity<String> beanValidationPathParameterInherited(Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @Override
    @GetExchange("/beanValidation/pathParameter/overridden")
    public ResponseEntity<String> beanValidationPathParameterOverridden(@RequestParam("param") Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @PostExchange("/beanValidation/body")
    public ResponseEntity<String> beanValidationBody(@Valid @RequestBody Model body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostExchange("/beanValidation/body/nested")
    public ResponseEntity<String> beanValidationBodyNested(@Valid @RequestBody NestedModel body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostExchange("/beanValidation/body/inheritance")
    public ResponseEntity<String> beanValidationBodyInheritance(@Valid @RequestBody ChildModel body) {
        return ResponseEntity.ok("body: " + body);
    }

    @PostExchange("/beanValidation/queryParameter/nested")
    public ResponseEntity<String> beanValidationQueryParameterNested(@Valid Model p) {
        return ResponseEntity.ok("param: " + p);
    }

    @PostExchange("/jackson/mismatchedInputException")
    public ResponseEntity<String> jacksonMismatchedInputException(@Valid @RequestBody JacksonModel p) {
        return ResponseEntity.ok("param: " + p);
    }

    @PostExchange("/i18n")
    public ResponseEntity<Void> i18n(@RequestParam("enabled") boolean enabled) {
        I18N.setEnabled(enabled);
        return ResponseEntity.ok().build();
    }

    @GetExchange("/requestValidator")
    public ResponseEntity<Void> requestValidator(@RequestParam("ssin") String ssin,
            @RequestParam(name = "a", required = false) String a,
            @RequestParam(name = "b", required = false) String b) {
        new RequestValidator().ssin(Input.query("ssin", ssin))
                .zeroOrAllOf(Input.query("a", a), Input.query("b", b))
                .validate();
        return ResponseEntity.ok().build();
    }

}
