package io.github.belgif.rest.problem.it;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.MatrixVariable;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/frontend/handler")
public class HandlerMethodValidationController {

    @GetMapping("/cookieValue")
    public ResponseEntity<String> cookieValue(@CookieValue("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @GetMapping("/matrixVariable/{path}")
    public ResponseEntity<String> matrixVariable(
            @PathVariable("path") String path, @MatrixVariable("param") @Positive @NotNull Integer param) {
        return ResponseEntity.ok("path: " + path + ", param: " + param);
    }

    @GetMapping("/pathVariable/{param}")
    public ResponseEntity<String> pathVariable(@PathVariable("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @GetMapping("/requestHeader")
    public ResponseEntity<String> requestHeader(@RequestHeader("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @PostMapping("/requestBody")
    public ResponseEntity<String> requestBody(@RequestBody @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

    @GetMapping("/requestParam")
    public ResponseEntity<String> requestParam(@RequestParam("param") @Positive @NotNull Integer p) {
        return ResponseEntity.ok("param: " + p);
    }

}
