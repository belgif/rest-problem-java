package io.github.belgif.rest.problem.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ProblemCommonConfiguration.class)
public class ProblemJackson2Configuration {

    @Bean
    public SpringProblemModule springProblemModule(SpringProblemTypeRegistry springProblemTypeRegistry) {
        return new SpringProblemModule(springProblemTypeRegistry);
    }
}
