package io.github.belgif.rest.problem.ee.jaxrs;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

@Priority(Priorities.USER + 200)
public class ProblemObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return ProblemObjectMapper.INSTANCE;
    }

}
