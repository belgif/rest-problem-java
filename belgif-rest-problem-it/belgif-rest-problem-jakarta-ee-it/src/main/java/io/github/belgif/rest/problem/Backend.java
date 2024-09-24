package io.github.belgif.rest.problem;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

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

}
