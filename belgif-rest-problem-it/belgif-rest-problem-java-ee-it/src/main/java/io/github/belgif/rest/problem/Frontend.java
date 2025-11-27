package io.github.belgif.rest.problem;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import javax.ws.rs.BeanParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import io.github.belgif.rest.problem.it.model.Bean;
import io.github.belgif.rest.problem.it.model.ChildModel;
import io.github.belgif.rest.problem.it.model.JacksonModel;
import io.github.belgif.rest.problem.it.model.Model;
import io.github.belgif.rest.problem.it.model.NestedModel;

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
    @Path("/ejb")
    Response ejb();

    @GET
    @Path("/retryAfter")
    Response retryAfter();

    @GET
    @Path("/okFromBackend")
    @Produces("application/json")
    Response okFromBackend(@QueryParam("client") Client client);

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
    @Path("/applicationJsonProblemFromBackend")
    Response applicationJsonProblemFromBackend(@QueryParam("client") Client client);

    @GET
    @Path("/jacksonMismatchedInputFromBackend")
    Response jacksonMismatchedInputFromBackend(@QueryParam("client") Client client);

    @GET
    @Path("/beanValidation/queryParameter")
    Response beanValidationQueryParameter(@QueryParam("param") @NotNull @Positive Integer p,
            @QueryParam("other") @Size(max = 5) String o);

    @GET
    @Path("/beanValidation/headerParameter")
    Response beanValidationHeaderParameter(@HeaderParam("param") @NotNull @Positive Integer p);

    @GET
    @Path("/beanValidation/pathParameter/inherited/{param}")
    Response beanValidationPathParameterInherited(@PathParam("param") @NotNull @Positive Integer p);

    @GET
    @Path("/beanValidation/pathParameter/overridden/{param}")
    Response beanValidationPathParameterOverridden(@PathParam("param") @NotNull @Positive Integer p);

    @POST
    @Path("/beanValidation/body")
    Response beanValidationBody(@Valid @NotNull Model body);

    @POST
    @Path("/beanValidation/body/nested")
    Response beanValidationBodyNested(@Valid NestedModel body);

    @POST
    @Path("/beanValidation/body/inheritance")
    Response beanValidationBodyInheritance(@Valid ChildModel body);

    @GET
    @Path("/beanValidation/beanParam/{name}")
    Response beanValidationBeanParam(@Valid @BeanParam Bean bean);

    @POST
    @Path("/jackson/mismatchedInputException")
    Response jacksonMismatchedInputException(@Valid JacksonModel body);

}
