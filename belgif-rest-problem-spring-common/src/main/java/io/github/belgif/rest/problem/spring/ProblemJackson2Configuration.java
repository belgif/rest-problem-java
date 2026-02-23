package io.github.belgif.rest.problem.spring;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ SpringProblemModule.class, ProblemConfigurationProperties.class, ProblemExtConfigurationProperties.class,
        SpringProblemTypeRegistry.class })
public class ProblemJackson2Configuration {
}
