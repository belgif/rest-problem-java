package io.github.belgif.rest.problem.quarkus.it;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/backend")
@RegisterRestClient(configKey = "backend")
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

}
