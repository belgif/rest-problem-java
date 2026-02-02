package io.github.belgif.rest.problem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

@Validated
@Controller
public interface ControllerHttpInterface {

    @GetExchange("/beanValidation/pathParameter/inherited/{param}")
    ResponseEntity<String> beanValidationPathParameterInherited(
            @PathVariable("param") @Positive @NotNull Integer p);

    @GetExchange("/beanValidation/pathParameter/overridden/{param}")
    ResponseEntity<String> beanValidationPathParameterOverridden(
            @PathVariable("param") @Positive @NotNull Integer p);

}
