package io.github.belgif.rest.problem.spring;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;

import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

/**
 * ProblemTypeRegistry implementation for Spring Boot applications.
 *
 * <p>
 * Uses component scanning to search the classpath for classes annotated with @ProblemType.
 * In addition to known problem base packages, additional packages can be scanned by configuring
 * io.github.belgif.rest.problem.scan-additional-problem-packages.
 * </p>
 *
 * @see ProblemType
 */
@Component
public class SpringProblemTypeRegistry implements ProblemTypeRegistry {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringProblemTypeRegistry.class);

    private static final List<String> DEFAULT_SCAN_PACKAGES = Arrays.asList("io.github.belgif.rest.problem");

    private final Map<String, Class<?>> problemTypes = new HashMap<>();

    public SpringProblemTypeRegistry(ProblemConfigurationProperties configuration) {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(ProblemType.class));
        List<String> packagesToScan = new ArrayList<>(DEFAULT_SCAN_PACKAGES);
        packagesToScan.addAll(configuration.getScanAdditionalProblemPackages());
        for (String packageToScan : packagesToScan) {
            Set<BeanDefinition> definitions = scanner.findCandidateComponents(packageToScan);
            for (BeanDefinition beanDefinition : definitions) {
                try {
                    Class<?> clazz = Class.forName(beanDefinition.getBeanClassName());
                    String type = clazz.getAnnotation(ProblemType.class).value();
                    LOGGER.debug("Registered problem {}: {}", clazz, type);
                    problemTypes.put(type, clazz);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException(e);
                }
            }

        }
    }

    @Override
    public Map<String, Class<?>> getProblemTypes() {
        return Collections.unmodifiableMap(problemTypes);
    }

}
