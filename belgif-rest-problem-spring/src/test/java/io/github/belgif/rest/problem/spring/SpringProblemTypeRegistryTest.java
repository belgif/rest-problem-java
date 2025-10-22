package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.jsontype.NamedType;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.ClientProblem;
import io.github.belgif.rest.problem.api.ProblemType;
import io.github.belgif.rest.problem.registry.ProblemTypeRegistry;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = { ProblemAutoConfiguration.class, JacksonAutoConfiguration.class })
class SpringProblemTypeRegistryTest {

    @Autowired
    private ProblemTypeRegistry problemTypeRegistry;

    @Test
    void getProblemTypes() {
        assertThat(problemTypeRegistry.getProblemTypes()).isNotEmpty();
        assertThat(problemTypeRegistry.getProblemTypes())
                .contains(new NamedType(BadRequestProblem.class, BadRequestProblem.TYPE));
        assertThat(problemTypeRegistry.getProblemTypes())
                .contains(new NamedType(MyProblem.class, "urn:problem-type:cbss:test:custom"));
    }

    @ProblemType("urn:problem-type:cbss:test:custom")
    public static class MyProblem extends ClientProblem {
        MyProblem() {
            super(URI.create("urn:problem-type:cbss:test:custom"), "My Problem", 400);
        }
    }

}
