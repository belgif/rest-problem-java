package io.github.belgif.rest.problem;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Validated
@Controller
public interface ControllerInterface {

    @GetMapping("/constraintViolationPath/{id}")
    ResponseEntity<String> constraintViolationPath(@Valid @PathVariable("id") @Min(3) @Max(10) int id);

    @GetMapping("/overriddenPath/{id}")
    ResponseEntity<String> overriddenPath(@Valid @PathVariable("id") @Min(3) @Max(10) int id);

}
