package io.github.belgif.rest.problem;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("/backend")
public interface Backend {

    @GET
    @Path("/ok")
    Response ok();

    @GET
    @Path("/badRequest")
    Response badRequest();

    @GET
    @Path("/custom")
    Response custom();

    @GET
    @Path("/unmapped")
    Response unmapped();

    @GET
    @Path("/applicationJsonProblem")
    Response applicationJsonProblem();

    @GET
    @Path("/jacksonMismatchedInput")
    Response jacksonMismatchedInput();

}
