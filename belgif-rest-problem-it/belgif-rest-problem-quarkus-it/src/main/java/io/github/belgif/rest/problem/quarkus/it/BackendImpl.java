package io.github.belgif.rest.problem.quarkus.it;

import java.net.URI;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.it.model.JacksonModel;

@RequestScoped
public class BackendImpl implements Backend {

    @Override
    public Response ok() {
        return Response.ok("OK").build();
    }

    @Override
    public Response badRequest() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request from backend");
        throw problem;
    }

    @Override
    public Response custom() {
        throw new CustomProblem("value from backend");
    }

    @Override
    public Response unmapped() {
        Problem unmapped = new Problem(URI.create("urn:problem-type:belgif:test:unmapped"), "Unmapped problem", 400) {
        };
        unmapped.setDetail("Unmapped problem from backend");
        throw unmapped;
    }

    @Override
    public Response applicationJsonProblem() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request with application/json media type from backend");
        return Response.status(Response.Status.BAD_REQUEST)
                .type(MediaType.APPLICATION_JSON_TYPE)
                .entity(problem)
                .build();
    }

    @Override
    public Response jacksonMismatchedInput() {
        JacksonModel model = new JacksonModel(null);
        model.setDescription("description");
        return Response.ok(model, MediaType.APPLICATION_JSON_TYPE).build();
    }

}
