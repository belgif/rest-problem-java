package io.github.belgif.rest.problem.ee.jaxrs.client;

import javax.ws.rs.client.ResponseProcessingException;

import io.github.belgif.rest.problem.api.Problem;

/**
 * ResponseProcessingException that wraps a Problem.
 *
 * <p>
 * If a Problem exception is thrown from a JAX-RS ClientResponseFilter, it would get wrapped in a
 * ResponseProcessingException anyway. By wrapping it in our own ResponseProcessingException subclass,
 * we at least have some more control over it.
 * </p>
 *
 * @see javax.ws.rs.client.ClientResponseFilter
 */
public class ProblemWrapper extends ResponseProcessingException {

    private final Problem problem;

    public ProblemWrapper(Problem problem) {
        super(null, problem);
        this.problem = problem;
    }

    public Problem getProblem() {
        return problem;
    }

}
