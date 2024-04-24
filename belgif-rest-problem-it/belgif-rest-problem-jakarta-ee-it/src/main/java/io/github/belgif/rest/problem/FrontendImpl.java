package io.github.belgif.rest.problem;

import java.net.URI;
import java.util.concurrent.ExecutionException;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.jboss.resteasy.client.jaxrs.internal.ResteasyClientBuilderImpl;

import com.acme.custom.CustomProblem;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.jaxrs.client.ProblemSupport;

@RequestScoped
public class FrontendImpl implements Frontend {

    private static final URI BASE_URI =
            URI.create("http://" + System.getProperty("jboss.bind.address") + ":8080/rest-problem");

    private final Backend microprofileClient = RestClientBuilder.newBuilder()
            .baseUri(BASE_URI)
            .build(Backend.class);

    private jakarta.ws.rs.client.Client jaxRsClient;

    private final jakarta.ws.rs.client.Client resteasyClient =
            ProblemSupport.enable(new ResteasyClientBuilderImpl().build());

    private final Backend resteasyProxyClient = ProblemSupport.enable(
            new ResteasyClientBuilderImpl().build().target(BASE_URI).proxy(Backend.class));

    @Inject
    public void setJaxRsClient(jakarta.ws.rs.client.Client jaxRsClient) {
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
                } catch (InterruptedException | ExecutionException e) {
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
                } catch (InterruptedException | ExecutionException e) {
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
                } catch (InterruptedException | ExecutionException e) {
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
    public Response beanValidation(String required, Integer positive) {
        return Response.ok().build();
    }

}
