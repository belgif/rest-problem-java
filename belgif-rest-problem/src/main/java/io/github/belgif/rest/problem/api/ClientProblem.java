package io.github.belgif.rest.problem.api;

import java.net.URI;

/**
 * Abstract base class for client problems (HTTP status code 4xx).
 */
public abstract class ClientProblem extends Problem {

    protected ClientProblem(URI type, String title, Integer status) {
        super(type, title, status);
    }

    protected ClientProblem(URI type, URI href, String title, Integer status) {
        super(type, href, title, status);
    }

    protected ClientProblem(URI type, String title, Integer status, Throwable cause) {
        super(type, title, status, cause);
    }

    protected ClientProblem(URI type, URI href, String title, Integer status, Throwable cause) {
        super(type, href, title, status, cause);
    }

}
