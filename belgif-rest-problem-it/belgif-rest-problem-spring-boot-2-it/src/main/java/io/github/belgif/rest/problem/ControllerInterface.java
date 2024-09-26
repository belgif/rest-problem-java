package io.github.belgif.rest.problem;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Validated
@Controller
public interface ControllerInterface {

    @GetMapping("/beanValidation/pathParameter/inherited/{param}")
    ResponseEntity<String> beanValidationPathParameterInherited(
            @PathVariable("param") @Positive @NotNull Integer p);

    @GetMapping("/beanValidation/pathParameter/overridden/{param}")
    ResponseEntity<String> beanValidationPathParameterOverridden(
            @PathVariable("param") @Positive @NotNull Integer p);

}
