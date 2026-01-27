package io.github.belgif.rest.problem.spring.internal;

import java.lang.annotation.Annotation;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;

import io.github.belgif.rest.problem.config.DisableProblems;
import io.github.belgif.rest.problem.config.EnableProblems;
import io.github.belgif.rest.problem.config.ProblemConfig;

public class ProblemRestControllerSupport {

    private ProblemRestControllerSupport() {
    }

    public static boolean isServerSideDisabled() {
        return !isServerSideEnabled();
    }

    public static boolean isServerSideEnabled() {
        return (ProblemConfig.isServerSideEnabled() && !hasControllerAnnotation(DisableProblems.class))
                || hasControllerAnnotation(EnableProblems.class);
    }

    private static boolean hasControllerAnnotation(Class<? extends Annotation> annotation) {
        if (RequestContextHolder.getRequestAttributes() instanceof ServletRequestAttributes attributes
                && attributes.getRequest().getAttribute(
                        HandlerMapping.BEST_MATCHING_HANDLER_ATTRIBUTE) instanceof HandlerMethod handler) {
            return handler.getBeanType().isAnnotationPresent(annotation);
        }
        return false;
    }

}
