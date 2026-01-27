package io.github.belgif.rest.enabled;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class Enabled {

    @GET
    @Path("/runtime")
    public Response runtime() {
        throw new RuntimeException("oops");
    }

}
