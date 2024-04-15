package io.github.belgif.rest.problem.jaxrs;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.CdiProblemModule;

@Provider
@Priority(Priorities.USER + 200)
public class ProblemObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new CdiProblemModule());

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return OBJECT_MAPPER;
    }

    public static ObjectMapper getObjectMapper() {
        return OBJECT_MAPPER;
    }

}
