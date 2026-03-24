package io.github.belgif.rest.problem.spring;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ProblemCommonConfiguration.class)
public class ProblemJackson3Configuration {

    @Bean
    public SpringProblemModuleJackson3 springProblemModule(SpringProblemTypeRegistry springProblemTypeRegistry) {
        return new SpringProblemModuleJackson3(springProblemTypeRegistry);
    }
}
