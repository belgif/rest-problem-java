package io.github.belgif.rest.problem.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson2ErrorHandler;
import io.github.belgif.rest.problem.spring.client.ProblemResponseJackson3ErrorHandler;
import io.github.belgif.rest.problem.spring.server.AnnotationParameterNameProvider;
import io.github.belgif.rest.problem.spring.server.BeanValidationExceptionsHandler;
import io.github.belgif.rest.problem.spring.server.InvalidRequestExceptionJackson2Handler;
import io.github.belgif.rest.problem.spring.server.InvalidRequestExceptionJackson3Handler;
import io.github.belgif.rest.problem.spring.server.ProblemExceptionHandler;
import io.github.belgif.rest.problem.spring.server.RoutingExceptionsJackson2Handler;
import io.github.belgif.rest.problem.spring.server.RoutingExceptionsJackson3Handler;

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
     * Jackson version being used
     */
    JacksonVersion jacksonVersion() default JacksonVersion.JACKSON_3;

    enum JacksonVersion {
        JACKSON_2, JACKSON_3
    }

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
            MergedAnnotation<EnableProblemModule> annotation =
                    importingClassMetadata.getAnnotations().get(EnableProblemModule.class);
            boolean includeServer = annotation.getBoolean("server");
            boolean includeClient = annotation.getBoolean("client");
            boolean useJackson3 =
                    annotation.getEnum("jacksonVersion", JacksonVersion.class) == JacksonVersion.JACKSON_3;
            boolean includeBeanValidation = annotation.getBoolean("beanValidation");
            boolean includeSwaggerRequestValidator = annotation.getBoolean("swaggerRequestValidator");

            List<String> imports = new ArrayList<>();

            Class commonConfig = useJackson3 ? ProblemJackson3Configuration.class : ProblemJackson2Configuration.class;
            imports.add(commonConfig.getName());

            if (includeServer) {
                imports.add(ProblemExceptionHandler.class.getName());
                Class routingExceptionsHandler =
                        useJackson3 ? RoutingExceptionsJackson3Handler.class : RoutingExceptionsJackson2Handler.class;
                imports.add(routingExceptionsHandler.getName());

                if (includeBeanValidation) {
                    imports.add(BeanValidationConfiguration.class.getName());
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

    @Configuration
    @Import(BeanValidationExceptionsHandler.class)
    class BeanValidationConfiguration {
        @Bean
        public LocalValidatorFactoryBean validator() {
            LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
            validator.setConfigurationInitializer(
                    configuration -> configuration.parameterNameProvider(new AnnotationParameterNameProvider()));
            return validator;
        }
    }
}
