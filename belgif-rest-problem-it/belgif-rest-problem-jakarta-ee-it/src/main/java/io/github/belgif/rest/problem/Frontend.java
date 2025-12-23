package io.github.belgif.rest.problem;

import java.time.LocalDate;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import jakarta.ws.rs.BeanParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

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
    @Path("/beanValidation/queryParameter/date")
    Response beanValidationQueryParameterDate(@QueryParam("date") @NotNull LocalDate date);

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
    @Path("/beanValidation/beanParameter/{name}")
    Response beanValidationBeanParameter(@Valid @BeanParam Bean bean);

    @POST
    @Path("/jackson/mismatchedInputException")
    Response jacksonMismatchedInputException(@Valid JacksonModel body);

}
