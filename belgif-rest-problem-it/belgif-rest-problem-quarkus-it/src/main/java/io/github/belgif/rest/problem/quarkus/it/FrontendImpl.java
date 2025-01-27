package io.github.belgif.rest.problem.quarkus.it;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.DefaultProblem;
import io.github.belgif.rest.problem.ServiceUnavailableProblem;
import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.i18n.I18N;
import io.github.belgif.rest.problem.it.model.ChildModel;
import io.github.belgif.rest.problem.it.model.JacksonModel;
import io.github.belgif.rest.problem.it.model.Model;
import io.github.belgif.rest.problem.it.model.NestedModel;
import io.quarkus.rest.client.reactive.QuarkusRestClientBuilder;
import io.vertx.core.http.HttpServerRequest;

@RequestScoped
@Path("/frontend")
public class FrontendImpl implements Frontend {

    @Context
    private HttpServerRequest serverRequest;

    private URI baseUri;

    private Backend microprofileClient;

    @RestClient
    private Backend restClientBuilderClient;

    private Backend quarkusRestClientBuilderClient;

    private jakarta.ws.rs.client.Client jaxRsClient;

    @PostConstruct
    public void initialize() {
        this.baseUri = URI.create("http://" + serverRequest.localAddress().host() + ":"
                + serverRequest.localAddress().port() + "/quarkus");
        this.microprofileClient = RestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .build(Backend.class);
        this.quarkusRestClientBuilderClient = QuarkusRestClientBuilder.newBuilder()
                .baseUri(baseUri)
                .build(Backend.class);
    }

    @Inject
    public void setJaxRsClient(jakarta.ws.rs.client.Client jaxRsClient) {
        this.jaxRsClient = jaxRsClient;
    }

    @GET
    @Path("/ping")
    public Response ping() {
        return Response.ok("pong").build();
    }

    @Override
    public Response badRequest() {
        BadRequestProblem problem = new BadRequestProblem();
        problem.setDetail("Bad Request from frontend");
        throw problem;
    }

    @Override
    public Response custom() {
        throw new CustomProblem("value from frontend");
    }

    @Override
    public Response runtime() {
        throw new RuntimeException("oops");
    }

    @Override
    public Response unmapped() {
        Problem unmapped = new Problem(URI.create("urn:problem-type:belgif:test:unmapped"), "Unmapped problem", 400) {
        };
        unmapped.setDetail("Unmapped problem from frontend");
        throw unmapped;
    }

    @Override
    public Response retryAfter() {
        ServiceUnavailableProblem problem = new ServiceUnavailableProblem();
        problem.setRetryAfterSec(10000L);
        throw problem;
    }

    @Override
    public Response okFromBackend(Client client) {
        String result = null;
        if (client == null || client == Client.MICROPROFILE) {
            result = microprofileClient.ok().readEntity(String.class);
        } else if (client == Client.REGISTER_REST_CLIENT) {
            result = restClientBuilderClient.ok().readEntity(String.class);
        } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
            result = quarkusRestClientBuilderClient.ok().readEntity(String.class);
        } else if (client == Client.JAXRS) {
            result = jaxRsClient.target(baseUri).path("backend/ok").request().get().readEntity(String.class);
        } else if (client == Client.JAXRS_ASYNC) {
            try {
                result = jaxRsClient.target(baseUri).path("backend/ok").request().async().get().get()
                        .readEntity(String.class);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        return Response.ok(result).build();
    }

    @Override
    public Response badRequestFromBackend(Client client) {
        try {
            if (client == null || client == Client.MICROPROFILE) {
                return microprofileClient.badRequest();
            } else if (client == Client.REGISTER_REST_CLIENT) {
                return restClientBuilderClient.badRequest();
            } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
                return quarkusRestClientBuilderClient.badRequest();
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(baseUri).path("backend/badRequest").request().get();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(baseUri).path("backend/badRequest").request().async().get().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalStateException("Unsupported client " + client);
        } catch (BadRequestProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @Override
    public Response customFromBackend(@QueryParam("client") Client client) {
        try {
            if (client == null || client == Client.MICROPROFILE) {
                return microprofileClient.custom();
            } else if (client == Client.REGISTER_REST_CLIENT) {
                return restClientBuilderClient.custom();
            } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
                return quarkusRestClientBuilderClient.custom();
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(baseUri).path("backend/custom").request().buildGet().invoke();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(baseUri).path("backend/custom").request().buildGet().submit().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalStateException("Unsupported client " + client);
        } catch (CustomProblem e) {
            e.setCustomField(e.getCustomField() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @Override
    public Response unmappedFromBackend(@QueryParam("client") Client client) {
        try {
            if (client == null || client == Client.MICROPROFILE) {
                return microprofileClient.unmapped();
            } else if (client == Client.REGISTER_REST_CLIENT) {
                return restClientBuilderClient.unmapped();
            } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
                return quarkusRestClientBuilderClient.unmapped();
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(baseUri).path("backend/unmapped").request().get();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(baseUri).path("backend/unmapped").request().async().get().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalStateException("Unsupported client " + client);
        } catch (DefaultProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @Override
    public Response applicationJsonProblemFromBackend(Client client) {
        try {
            if (client == null || client == Client.MICROPROFILE) {
                return microprofileClient.applicationJsonProblem();
            } else if (client == Client.REGISTER_REST_CLIENT) {
                return restClientBuilderClient.applicationJsonProblem();
            } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
                return quarkusRestClientBuilderClient.applicationJsonProblem();
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(baseUri).path("backend/applicationJsonProblem").request().get();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(baseUri).path("backend/applicationJsonProblem").request().async().get().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
            throw new IllegalStateException("Unsupported client " + client);
        } catch (BadRequestProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @Override
    public Response jacksonMismatchedInputFromBackend(Client client) {
        if (client == null || client == Client.MICROPROFILE) {
            microprofileClient.jacksonMismatchedInput().readEntity(JacksonModel.class);
        } else if (client == Client.REGISTER_REST_CLIENT) {
            restClientBuilderClient.jacksonMismatchedInput().readEntity(JacksonModel.class);
        } else if (client == Client.QUARKUS_REST_CLIENT_BUILDER) {
            quarkusRestClientBuilderClient.jacksonMismatchedInput().readEntity(JacksonModel.class);
        } else if (client == Client.JAXRS) {
            jaxRsClient.target(baseUri).path("backend/jacksonMismatchedInput").request().get(JacksonModel.class);
        } else if (client == Client.JAXRS_ASYNC) {
            try {
                jaxRsClient.target(baseUri).path("backend/jacksonMismatchedInput").request().async()
                        .get(JacksonModel.class).get();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(e);
            } catch (ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        throw new IllegalStateException("Unsupported client " + client);
    }

    @Override
    public Response beanValidationQueryParameter(Integer p, String o) {
        return Response.ok("param: " + p + ", other: " + o).build();
    }

    @Override
    public Response beanValidationHeaderParameter(Integer p) {
        return Response.ok("param: " + p).build();
    }

    @GET
    @Path("/beanValidation/pathParameter/class/{param}")
    public Response beanValidationPathParameter(@PathParam("param") @NotNull @Positive Integer p) {
        return Response.ok("param: " + p).build();
    }

    @Override
    public Response beanValidationPathParameterInherited(Integer p) {
        return Response.ok("param: " + p).build();
    }

    @Override
    @GET
    @Path("/beanValidation/pathParameter/overridden")
    public Response beanValidationPathParameterOverridden(@QueryParam("param") @NotNull @Positive Integer p) {
        return Response.ok("param: " + p).build();
    }

    @Override
    public Response beanValidationBody(Model body) {
        return Response.ok("body: " + body).build();
    }

    @Override
    public Response beanValidationBodyNested(NestedModel body) {
        return Response.ok("body: " + body).build();
    }

    @Override
    public Response beanValidationBodyInheritance(ChildModel body) {
        return Response.ok("body: " + body).build();
    }

    @Override
    public Response jacksonMismatchedInputException(JacksonModel body) {
        return Response.ok("body: " + body).build();
    }

    @POST
    @Path("/i18n")
    public Response i18n(@QueryParam("enabled") boolean enabled) {
        I18N.setEnabled(enabled);
        return Response.ok().build();
    }

}
