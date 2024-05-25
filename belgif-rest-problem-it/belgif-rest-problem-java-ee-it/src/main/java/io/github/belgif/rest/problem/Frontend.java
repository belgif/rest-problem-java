package io.github.belgif.rest.problem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import io.github.belgif.rest.problem.model.ChildModel;
import io.github.belgif.rest.problem.model.Model;
import io.github.belgif.rest.problem.model.NestedModel;

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
    @Path("/beanValidation/queryParameter")
    Response beanValidationQueryParameter(@QueryParam("param") @NotNull @Positive Integer param,
            @QueryParam("other") @Size(max = 5) String other);

    @GET
    @Path("/beanValidation/headerParameter")
    Response beanValidationHeaderParameter(@HeaderParam("param") @NotNull @Positive Integer param);

    @GET
    @Path("/beanValidation/pathParameter/inherited/{param}")
    Response beanValidationPathParameterInherited(@PathParam("param") @NotNull @Positive Integer param);

    @GET
    @Path("/beanValidation/pathParameter/overridden/{param}")
    Response beanValidationPathParameterOverridden(@PathParam("param") @NotNull @Positive Integer param);

    @POST
    @Path("/beanValidation/body")
    Response beanValidationBody(@Valid Model body);

    @POST
    @Path("/beanValidation/body/nested")
    Response beanValidationBodyNested(@Valid NestedModel body);

    @POST
    @Path("/beanValidation/body/inheritance")
    Response beanValidationBodyInheritance(@Valid ChildModel body);

}
