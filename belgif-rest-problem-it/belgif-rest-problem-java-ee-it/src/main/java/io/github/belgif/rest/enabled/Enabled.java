package io.github.belgif.rest.enabled;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@RequestScoped
@Path("/")
public class Enabled {

    @GET
    @Path("/runtime")
    public Response runtime() {
        throw new RuntimeException("oops");
    }

}
