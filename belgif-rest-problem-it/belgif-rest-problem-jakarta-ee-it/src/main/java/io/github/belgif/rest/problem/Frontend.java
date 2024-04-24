package io.github.belgif.rest.problem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

@Path("/frontend")
public interface Frontend {
    @GET
    @Path("/badRequest")
    Response badRequest();

    @GET
    @Path("/custom")
    Response custom();

    @GET
    @Path("/runtime")
    Response runtime();

    @GET
    @Path("/unmapped")
    Response unmapped();

    @GET
    @Path("/retryAfter")
    Response retryAfter();

    @GET
    @Path("/badRequestFromBackend")
    Response badRequestFromBackend(@QueryParam("client") Client client);

    @GET
    @Path("/customFromBackend")
    Response customFromBackend(@QueryParam("client") Client client);

    @GET
    @Path("/unmappedFromBackend")
    Response unmappedFromBackend(@QueryParam("client") Client client);

    @GET
    @Path("/beanValidation")
    Response beanValidation(@QueryParam("required") @NotNull String required,
            @QueryParam("positive") @Positive Integer positive);

}
