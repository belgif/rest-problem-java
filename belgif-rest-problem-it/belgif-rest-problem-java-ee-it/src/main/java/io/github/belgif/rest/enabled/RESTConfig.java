package io.github.belgif.rest.enabled;

import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.github.belgif.rest.problem.ee.jaxrs.ProblemFeature;

@ApplicationPath("/enabled")
public class RESTConfig extends Application {

    @Override
    public Set<Class<?>> getClasses() {
        return Set.of(Enabled.class);
    }

    @Override
    public Set<Object> getSingletons() {
        return Set.of(new ProblemFeature());
    }

}
