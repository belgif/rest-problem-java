package io.github.belgif.rest.problem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import io.github.belgif.rest.problem.openapi.validation.sb3.api.MyFirstPathApi;
import io.github.belgif.rest.problem.openapi.validation.sb3.api.MyHeaderPathApi;
import io.github.belgif.rest.problem.openapi.validation.sb3.model.MyRequestBodySchema;

@RestController
public class ValidationController implements MyFirstPathApi, MyHeaderPathApi {

    @Override
    public ResponseEntity<String> myFirstGetOperation(String myParam) {
        return buildResponse();
    }

    @Override
    public ResponseEntity<String> myFirstPostOperation(MyRequestBodySchema myRequestBodySchema) {
        return buildResponse();
    }

    @Override
    public ResponseEntity<String> mySecondGetOperation(String pathParam) {
        return buildResponse();
    }

    @Override
    public ResponseEntity<String> myHeaderGetOperation(String myHeader) {
        return buildResponse();
    }

    private ResponseEntity<String> buildResponse() {
        return ResponseEntity.ok("All good!");
    }

}
