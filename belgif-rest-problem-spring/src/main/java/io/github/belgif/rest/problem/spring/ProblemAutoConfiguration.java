package io.github.belgif.rest.problem.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;

/**
 * Spring Boot AutoConfiguration for rest-problem-spring.
 */
@AutoConfiguration
@ComponentScan("io.github.belgif.rest.problem")
@EnableConfigurationProperties({ ProblemConfigurationProperties.class, ProblemExtConfigurationProperties.class })
public class ProblemAutoConfiguration {

}
