package io.github.belgif.rest.problem.ee;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.AfterTypeDiscovery;
import javax.enterprise.inject.spi.Extension;
import javax.enterprise.inject.spi.ProcessAnnotatedType;
import javax.enterprise.inject.spi.WithAnnotations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.belgif.rest.problem.api.Problem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * CDI extension for dynamic discovery of {@link Problem} classes annotated with @{@link ProblemType}.
 *
 * <p>
 * This class exposes a {@code Map<String, Class>} that can be used to configure the Jackson Object mapper
 * for polymorphic deserialization of problem types.
 * </p>
 *
 * @see ProblemType
 */
@ApplicationScoped
public class CdiProblemTypeRegistry implements Extension, ProblemTypeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(CdiProblemTypeRegistry.class);

    private final Map<String, Class<?>> registeredProblemTypes = new HashMap<>();

    private Map<String, Class<?>> problemTypes;

    void processAnnotatedType(
            @Observes @WithAnnotations(ProblemType.class) ProcessAnnotatedType<? extends Problem> problemType) {
        ProblemType annotation = problemType.getAnnotatedType().getAnnotation(ProblemType.class);
        Class<? extends Problem> clazz = problemType.getAnnotatedType().getJavaClass();
        if (annotation == null) {
            // Even though we observe @WithAnnotations(ProblemType.class), it seems the method can also be called
            // for Problem subclasses without @ProblemType annotation. Not sure why that happens.
            LOGGER.debug("Skipping {}: no @ProblemType annotation found", clazz);
        } else {
            String type = annotation.value();
            LOGGER.debug("Registered problem {}: {}", clazz, type);
            registeredProblemTypes.put(type, clazz);
        }
        // no further processing required, CDI container can ignore this type
        problemType.veto();
    }

    void afterTypeDiscovery(@Observes AfterTypeDiscovery atd) {
        problemTypes = registeredProblemTypes;
    }

    @Override
    public Map<String, Class<?>> getProblemTypes() {
        return new HashMap<>(problemTypes);
    }

}
