package io.github.belgif.rest.problem.spring.server;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.github.belgif.rest.problem.spring.ProblemJackson3Configuration;

class ServerProblemAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ServerProblemAutoConfiguration.class));

    @Test
    void autoConfiguration() {
        contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(ProblemJackson3Configuration.class);
        });
    }

}
