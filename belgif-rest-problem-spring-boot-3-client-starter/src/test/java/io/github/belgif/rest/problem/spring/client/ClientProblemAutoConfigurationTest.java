package io.github.belgif.rest.problem.spring.client;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import io.github.belgif.rest.problem.spring.ProblemJackson2Configuration;

class ClientProblemAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(ClientProblemAutoConfiguration.class));

    @Test
    void autoConfiguration() {
        contextRunner.run((context) -> {
            assertThat(context).hasSingleBean(ProblemJackson2Configuration.class);
        });
    }
}
