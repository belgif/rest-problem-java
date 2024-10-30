package io.github.belgif.rest.problem;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.belgif.rest.problem.openapi.validation.sb3.api.*;
import io.github.belgif.rest.problem.openapi.validation.sb3.model.MyRequestBodySchema;
import io.github.belgif.rest.problem.openapi.validation.sb3.model.PostOperationWithAllOfSchemaRequest;
import io.github.belgif.rest.problem.openapi.validation.sb3.model.PostOperationWithOneOfSchemaRequest;

@RestController
@RequestMapping("/openapi-validation")
public class OpenApiValidationController
        implements MyFirstPathApi, MyHeaderPathApi, MyQueryPathApi {

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

    @Override
    public ResponseEntity<String> myQueryParamOperation(String myParam) {
        return buildResponse();
    }

    @Override
    public ResponseEntity<String>
            postOperationWithAllOfSchema(PostOperationWithAllOfSchemaRequest postOperationWithAllOfSchemaRequest) {
        return buildResponse();
    }

    @Override
    public ResponseEntity<String>
            postOperationWithOneOfSchema(PostOperationWithOneOfSchemaRequest postOperationWithOneOfSchemaRequest) {
        return buildResponse();
    }

    private ResponseEntity<String> buildResponse() {
        return ResponseEntity.ok("All good!");
    }

}
