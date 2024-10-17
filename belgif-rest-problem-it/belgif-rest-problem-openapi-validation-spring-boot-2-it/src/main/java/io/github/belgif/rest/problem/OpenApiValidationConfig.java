package io.github.belgif.rest.problem;

import javax.servlet.Filter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;

@Configuration
public class OpenApiValidationConfig {

    @Bean
    public Filter validationFilter() {
        return new OpenApiValidationFilter(true, false);
    }

    @Bean
    public WebMvcConfigurer addOpenApiValidationInterceptor(@Value("/openapi.yaml") final String apiSpecification,
            @Value("server.servlet.context-path") String contextPath) {
        final OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createForSpecificationUrl(apiSpecification)
                .withLevelResolver(LevelResolver.create()
                        // Accept additionalProperties even if they're not defined in the schema
                        .withLevel("validation.schema.additionalProperties", ValidationReport.Level.IGNORE)
                        // Ignores validation when a path is not in the openapi and let Spring handle the error
                        .withLevel("validation.request.path.missing", ValidationReport.Level.INFO)
                        .build())
                .withBasePathOverride(contextPath)
                .build();
        final OpenApiValidationInterceptor openApiValidationInterceptor = new OpenApiValidationInterceptor(validator);
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(@NonNull final InterceptorRegistry registry) {
                registry.addInterceptor(openApiValidationInterceptor);
            }
        };
    }

}
