package io.github.belgif.rest.problem;

import java.util.Collections;

import org.jspecify.annotations.NonNull;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.atlassian.oai.validator.OpenApiInteractionValidator;
import com.atlassian.oai.validator.report.LevelResolver;
import com.atlassian.oai.validator.report.ValidationReport;
import com.atlassian.oai.validator.springmvc.OpenApiValidationFilter;
import com.atlassian.oai.validator.springmvc.OpenApiValidationInterceptor;

@Configuration
public class OpenApiValidationConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<OpenApiValidationFilter> validationFilter() {
        FilterRegistrationBean<OpenApiValidationFilter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter(new OpenApiValidationFilter(true, false));
        filterRegistration.setUrlPatterns(Collections.singletonList("/openapi-validation/*"));
        return filterRegistration;
    }

    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        OpenApiInteractionValidator validator = OpenApiInteractionValidator
                .createForSpecificationUrl("/openapi.yaml")
                .withLevelResolver(LevelResolver.create()
                        // Accept additionalProperties even if they're not defined in the schema
                        .withLevel("validation.schema.additionalProperties", ValidationReport.Level.IGNORE)
                        .build())
                .withBasePathOverride("/openapi-validation")
                .build();
        registry.addInterceptor(new OpenApiValidationInterceptor(validator));
    }

}
