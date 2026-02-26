package io.github.belgif.rest.problem.spring;

import java.lang.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson2ErrorHandler;
import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson3ErrorHandler;
import io.github.belgif.rest.problem.spring.server.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableProblemModule.ProblemModuleImportSelector.class)
/**
 * Activate support of the belgif-rest-problem integration for spring.
 *
 * When running in a Spring Boot environment, use of the 'starter' modules is recommended, which require less
 * configuration.
 *
 * Attributes select which optional integrations are activated.
 * An application using this module, should provide an ObjectMapper bean on which the {@link SpringProblemModule}
 * or {@link SpringProblemModuleJackson3} support bean is registered.
 */
public @interface EnableProblemModule {

    /**
     * Activate RestControllerAdvice for rest-problem support on any RestController
     */
    boolean server() default true;

    /**
     * Creates support beans for REST clients.
     * Requires:
     * - setting ProblemResponseErrorHandler as defaultStatusHandler on the client
     * - or for {@link org.springframework.web.reactive.function.client.WebClient}, setting PROBLEM_FILTER from
     * {@link io.github.belgif.rest.problem.spring.client.WebClientFilter}
     */
    boolean client() default true;

    /**
     * Whether to create support beans for Jackson 3, or when false, Jackson 2.
     */
    boolean jackson3() default true;

    /**
     * Convert Jakarta Bean Validation exceptions to badRequestProblem
     *
     * Only supported server-side.
     * For proper problem messages, requires using the {@link AnnotationParameterNameProvider} on the used
     * {@link jakarta.validation.Configuration}
     */
    boolean beanValidation() default false;

    /**
     * Convert input validation exceptions from the swagger-request-validator library to badRequestProblem
     *
     * Only supported server-side
     */
    boolean swaggerRequestValidator() default false;

    class ProblemModuleImportSelector implements ImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            Map<String, Object> attributes = importingClassMetadata
                    .getAnnotationAttributes(EnableProblemModule.class.getName());
            boolean includeServer = (boolean) attributes.get("server");
            boolean includeClient = (boolean) attributes.get("client");
            boolean useJackson3 = (boolean) attributes.get("jackson3");
            boolean includeBeanValidation = (boolean) attributes.get("beanValidation");
            boolean includeSwaggerRequestValidator = (boolean) attributes.get("swaggerRequestValidator");

            List<String> imports = new ArrayList<>();

            Class commonConfig = useJackson3 ? ProblemJackson3Configuration.class : ProblemJackson2Configuration.class;
            imports.add(commonConfig.getName());

            if (includeServer) {
                imports.add(ProblemExceptionHandler.class.getName());
                Class routingExceptionsHandler =
                        useJackson3 ? RoutingExceptionsJackson3Handler.class : RoutingExceptionsJackson2Handler.class;
                imports.add(routingExceptionsHandler.getName());

                if (includeBeanValidation) {
                    imports.add(BeanValidationExceptionsHandler.class.getName());
                }
                if (includeSwaggerRequestValidator) {
                    Class invalidRequestExceptionHandler = useJackson3 ? InvalidRequestExceptionJackson3Handler.class
                            : InvalidRequestExceptionJackson2Handler.class;
                    imports.add(invalidRequestExceptionHandler.getName());
                }
            }
            if (includeClient) {
                Class clientConfig =
                        useJackson3 ? ProblemResponseJackson3ErrorHandler.class
                                : ProblemResponseJackson2ErrorHandler.class;
                imports.add(clientConfig.getName());
            }
            return imports.toArray(new String[imports.size()]);
        }
    }
}
