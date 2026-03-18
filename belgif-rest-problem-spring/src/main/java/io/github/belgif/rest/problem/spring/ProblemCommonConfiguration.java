package io.github.belgif.rest.problem.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProblemCommonConfiguration {

    @Bean
    ProblemConfigurationProperties problemConfigurationProperties() {
        return new ProblemConfigurationProperties();
    }

    @Bean
    public ProblemExtConfigurationProperties problemExtConfigurationProperties() {
        return new ProblemExtConfigurationProperties();
    }

    @Bean
    public SpringProblemTypeRegistry
            springProblemTypeRegistry(ProblemConfigurationProperties problemConfigurationProperties) {
        return new SpringProblemTypeRegistry(problemConfigurationProperties);
    }
}
