package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.jaxrs.client.ProblemSupport;
import io.github.belgif.rest.problem.model.ChildModel;
import io.github.belgif.rest.problem.model.Model;
import io.github.belgif.rest.problem.model.NestedModel;

@RequestScoped
@Path("/frontend")
public class FrontendImpl implements Frontend {

    private static final URI BASE_URI =
            URI.create("http://" + System.getProperty("jboss.bind.address") + ":8080/rest-problem");

    private final Backend microprofileClient = RestClientBuilder.newBuilder()
            .baseUri(BASE_URI)
            .build(Backend.class);

    private javax.ws.rs.client.Client jaxRsClient;

    private final javax.ws.rs.client.Client resteasyClient = ProblemSupport.enable(new ResteasyClientBuilder().build());

    private final Backend resteasyProxyClient = ProblemSupport.enable(
            new ResteasyClientBuilder().build().target(BASE_URI).proxy(Backend.class));

    @Inject
    public void setJaxRsClient(javax.ws.rs.client.Client jaxRsClient) {
        this.jaxRsClient = jaxRsClient;
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
    public Response badRequestFromBackend(Client client) {
        try {
            if (client == null || client == Client.MICROPROFILE) {
                return microprofileClient.badRequest();
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(BASE_URI).path("backend/badRequest").request().get();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(BASE_URI).path("backend/badRequest").request().async().get().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else if (client == Client.RESTEASY) {
                return resteasyClient.target(BASE_URI).path("backend/badRequest").request().get();
            } else if (client == Client.RESTEASY_PROXY) {
                return resteasyProxyClient.badRequest();
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
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(BASE_URI).path("backend/custom").request().buildGet().invoke();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(BASE_URI).path("backend/custom").request().buildGet().submit().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else if (client == Client.RESTEASY) {
                return resteasyClient.target(BASE_URI).path("backend/custom").request().get();
            } else if (client == Client.RESTEASY_PROXY) {
                return resteasyProxyClient.custom();
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
            } else if (client == Client.JAXRS) {
                return jaxRsClient.target(BASE_URI).path("backend/unmapped").request().get();
            } else if (client == Client.JAXRS_ASYNC) {
                try {
                    jaxRsClient.target(BASE_URI).path("backend/unmapped").request().async().get().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException(e);
                } catch (ExecutionException e) {
                    throw new RuntimeException(e);
                }
            } else if (client == Client.RESTEASY) {
                return resteasyClient.target(BASE_URI).path("backend/unmapped").request().get();
            } else if (client == Client.RESTEASY_PROXY) {
                return resteasyProxyClient.unmapped();
            }
            throw new IllegalStateException("Unsupported client " + client);
        } catch (DefaultProblem e) {
            e.setDetail(e.getDetail() + " (caught successfully by frontend)");
            throw e;
        }
    }

    @Override
    public Response beanValidationQueryParameter(Integer param, String other) {
        return Response.ok("param: " + param + ", other: " + other).build();
    }

    @Override
    public Response beanValidationHeaderParameter(Integer param) {
        return Response.ok("header: " + param).build();
    }

    @GET
    @Path("/beanValidation/pathParameter/class/{param}")
    public Response beanValidationPathParameter(@PathParam("param") @NotNull @Positive Integer param) {
        return Response.ok("param: " + param).build();
    }

    @Override
    public Response beanValidationPathParameterInherited(Integer param) {
        return Response.ok("param: " + param).build();
    }

    @Override
    @GET
    @Path("/beanValidation/pathParameter/overridden")
    public Response beanValidationPathParameterOverridden(@QueryParam("param") @NotNull @Positive Integer param) {
        return Response.ok("param: " + param).build();
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
}
