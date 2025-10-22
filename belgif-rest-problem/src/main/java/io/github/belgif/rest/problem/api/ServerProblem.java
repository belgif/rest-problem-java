package io.github.belgif.rest.problem.api;

import java.net.URI;

/**
 * Abstract base class for server problems (HTTP status code 5xx).
 */
public abstract class ServerProblem extends Problem {

    protected ServerProblem(URI type, String title, Integer status) {
        super(type, title, status);
    }

    protected ServerProblem(URI type, URI href, String title, Integer status) {
        super(type, href, title, status);
    }

    protected ServerProblem(URI type, String title, Integer status, Throwable cause) {
        super(type, title, status, cause);
    }

    protected ServerProblem(URI type, URI href, String title, Integer status, Throwable cause) {
        super(type, href, title, status, cause);
    }

}
