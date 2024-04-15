package io.github.belgif.rest.problem.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;

import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * Annotation used on {@link Problem} subclasses for configuring the problem type URI,
 * used for Jackson polymorphic deserialization.
 *
 * @see ProblemTypeRegistry
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Stereotype
// This @Stereotype annotation is required for CDI integration.
// Given that annotations that are not found on the classpath are ignored,
// no CDI-api runtime dependency is required (e.g. for Spring Boot).
public @interface ProblemType {
    String value();
}
