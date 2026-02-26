package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ProblemJackson2Configuration.class })
class SpringProblemTypeRegistryTest {

    @Autowired
    private ProblemTypeRegistry problemTypeRegistry;

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Test
    void getProblemTypes() {
        assertThat(problemTypeRegistry.getProblemTypes()).isNotEmpty();
        assertThat(problemTypeRegistry.getProblemTypes())
                .containsEntry(BadRequestProblem.TYPE, BadRequestProblem.class);
        assertThat(problemTypeRegistry.getProblemTypes())
                .containsEntry("urn:problem-type:cbss:test:custom", MyProblem.class);
    }

    @ProblemType("urn:problem-type:cbss:test:custom")
    public static class MyProblem extends ClientProblem {
        MyProblem() {
            super(URI.create("urn:problem-type:cbss:test:custom"), "My Problem", 400);
        }
    }

}
