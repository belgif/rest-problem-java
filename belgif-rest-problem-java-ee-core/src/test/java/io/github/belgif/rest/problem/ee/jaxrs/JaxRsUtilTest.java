package io.github.belgif.rest.problem.ee.jaxrs;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.api.Problem;

class JaxRsUtilTest {

    @Test
    void register() {
        Configurable configurable = mock(Configurable.class);
        Configuration configuration = mock(Configuration.class);
        when(configurable.getConfiguration()).thenReturn(configuration);
        Class<?> componentClass = JaxRsUtilTest.class;
        when(configuration.isRegistered(componentClass)).thenReturn(false);
        JaxRsUtil.register(configurable, componentClass);
        verify(configurable).register(componentClass);
    }

    @Test
    void registerAlreadyRegistered() {
        Configurable configurable = mock(Configurable.class);
        Configuration configuration = mock(Configuration.class);
        when(configurable.getConfiguration()).thenReturn(configuration);
        Class<?> componentClass = JaxRsUtilTest.class;
        when(configuration.isRegistered(componentClass)).thenReturn(true);
        JaxRsUtil.register(configurable, componentClass);
        verify(configurable, never()).register(componentClass);
    }

    @Test
    void locateObjectMapperFromProviders() {
        Providers providers = mock(Providers.class);
        ContextResolver<ObjectMapper> contextResolver = mock(ContextResolver.class);
        ObjectMapper mapper = new ObjectMapper();
        when(providers.getContextResolver(ObjectMapper.class, MediaType.APPLICATION_JSON_TYPE))
                .thenReturn(contextResolver);
        when(contextResolver.getContext(Problem.class)).thenReturn(mapper);

        ObjectMapper result = JaxRsUtil.locateObjectMapper(providers, null, Problem.class,
                MediaType.APPLICATION_JSON_TYPE, null);
        assertThat(result).isSameAs(mapper);
    }

    @Test
    void locateObjectMapperFromCdiInstance() {
        Instance<ObjectMapper> cdiObjectMapper = mock(Instance.class);
        ObjectMapper mapper = new ObjectMapper();
        when(cdiObjectMapper.isResolvable()).thenReturn(true);
        when(cdiObjectMapper.get()).thenReturn(mapper);

        ObjectMapper result = JaxRsUtil.locateObjectMapper(null, cdiObjectMapper, Problem.class,
                MediaType.APPLICATION_JSON_TYPE, null);
        assertThat(result).isSameAs(mapper);
    }

    @Test
    void locateObjectMapperFromCdiStatic() {
        CDI cdi = mock(CDI.class);
        Instance<ObjectMapper> cdiObjectMapper = mock(Instance.class);
        try (MockedStatic<CDI> mock = mockStatic(CDI.class)) {
            mock.when(CDI::current).thenReturn(cdi);
            when(cdi.select(ObjectMapper.class)).thenReturn(cdiObjectMapper);
            ObjectMapper mapper = new ObjectMapper();
            when(cdiObjectMapper.isResolvable()).thenReturn(true);
            when(cdiObjectMapper.get()).thenReturn(mapper);
            ObjectMapper result = JaxRsUtil.locateObjectMapper(null, null, Problem.class,
                    MediaType.APPLICATION_JSON_TYPE, null);
            assertThat(result).isSameAs(mapper);
        }
    }

    @Test
    void locateObjectMapperFallback() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectMapper result = JaxRsUtil.locateObjectMapper(null, null, Problem.class,
                MediaType.APPLICATION_JSON_TYPE, () -> mapper);
        assertThat(result).isSameAs(mapper);
    }

}
