package io.github.belgif.rest.problem.ee.jaxrs;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import io.github.belgif.rest.problem.ee.internal.Platform;

public class ProblemFeature implements Feature {

    public static final Class<?>[] DEFAULT_PROVIDER_CLASSES = {
            ProblemObjectMapperContextResolver.class,
            JaxRsParameterNameProvider.class,
            JacksonJsonMappingExceptionMapper.class,
            JacksonJsonParseExceptionMapper.class,
            JacksonMismatchedInputExceptionMapper.class,
            ConstraintViolationExceptionMapper.class,
            WebApplicationExceptionMapper.class,
            NotFoundExceptionMapper.class,
            BadRequestExceptionMapper.class,
            ProblemExceptionMapper.class,
            DefaultExceptionMapper.class
    };

    @Override
    public boolean configure(FeatureContext featureContext) {
        for (Class<?> providerClass : DEFAULT_PROVIDER_CLASSES) {
            featureContext.register(providerClass);
        }
        if (!Platform.isQuarkus()) {
            // Quarkus does not support EJB
            try {
                featureContext.register(Class.forName("io.github.belgif.rest.problem.ee.jaxrs.EJBExceptionMapper"));
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
        return true;
    }

}
