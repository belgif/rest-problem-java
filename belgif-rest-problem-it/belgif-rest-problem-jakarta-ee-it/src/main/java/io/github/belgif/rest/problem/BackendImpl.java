package io.github.belgif.rest.problem;

import java.net.URI;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.core.Response;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;

@RequestScoped
public class BackendImpl implements Backend {

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

}
