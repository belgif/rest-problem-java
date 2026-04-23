package io.github.belgif.rest.problem.ee.core.jaxrs;

import java.util.function.Supplier;

import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Providers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Utility class for JAX-RS components.
 */
public class JaxRsUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(JaxRsUtil.class);

    private JaxRsUtil() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Register the given component class on the given Configurable (only if no yet registered).
     *
     * @param configurable the {@link Configurable}
     * @param componentClass the component class
     */
    public static void register(Configurable<?> configurable, Class<?> componentClass) {
        if (!configurable.getConfiguration().isRegistered(componentClass)) {
            configurable.register(componentClass);
        }
    }

    /**
     * Locate the Jackson ObjectMapper from the runtime platform, in the following order of precedence:
     * <ul>
     * <li>from JAX-RS {@code ContextResolver<ObjectMapper>}</li>
     * <li>from CDI {@code @Inject Instance<ObjectMapper>}</li>
     * <li>from static CDI.current()</li>
     * <li>fallback to the supplied default</li>
     * </ul>
     *
     * @param providers the JAX-RS Providers
     * @param cdiObjectMapper the {@code @Inject Instance<ObjectMapper>}
     * @param entityClass the entity class
     * @param mediaType the media type
     * @param fallback the ObjectMapper supplier to use as fallback
     * @return the ObjectMapper
     */
    public static ObjectMapper locateObjectMapper(Providers providers, Instance<ObjectMapper> cdiObjectMapper,
            Class<?> entityClass, MediaType mediaType, Supplier<ObjectMapper> fallback) {
        // First, try the JAX-RS providers
        if (providers != null) {
            ContextResolver<ObjectMapper> resolver =
                    providers.getContextResolver(ObjectMapper.class, mediaType);
            if (resolver != null) {
                ObjectMapper result = resolver.getContext(entityClass);
                if (result != null) {
                    return result;
                }
            }
        }
        // Then, try the CDI ObjectMapper
        if (cdiObjectMapper != null && cdiObjectMapper.isResolvable()) {
            ObjectMapper result = cdiObjectMapper.get();
            if (result != null) {
                return result;
            }
        }
        // Then, try static CDI access
        try {
            Instance<ObjectMapper> instance = CDI.current().select(ObjectMapper.class);
            if (instance != null && instance.isResolvable()) {
                ObjectMapper result = instance.get();
                if (result != null) {
                    return result;
                }
            }
        } catch (IllegalStateException e) {
            // CDI is not available
        }
        LOGGER.warn("ObjectMapper could not be retrieved from CDI or JAX-RS context, falling back to default");
        // Use fallback as last resort
        return fallback.get();
    }

}
