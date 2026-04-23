package io.github.belgif.rest.problem.ee.client.jaxrs;

import javax.annotation.Priority;
import javax.ws.rs.Priorities;
import javax.ws.rs.ext.ContextResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.ee.core.jaxrs.ProblemObjectMapper;

/**
 * Separate client-side ObjectMapper ContextResolver.
 *
 * <p>
 * Workaround for a weird bug in JBoss EAP XP MicroProfile REST client:
 * java.lang.ArrayIndexOutOfBoundsException: Index 0 out of bounds for length 0
 * at org.jboss.resteasy.spi.ResteasyProviderFactory.addContextResolver(ResteasyProviderFactory.java:1518)
 * If the ContextResolver class is not annotated with @Provider it works as expected.
 * </p>
 */
@Priority(Priorities.USER + 200)
public class ClientProblemObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

    @Override
    public ObjectMapper getContext(Class<?> type) {
        return ProblemObjectMapper.INSTANCE;
    }

}
