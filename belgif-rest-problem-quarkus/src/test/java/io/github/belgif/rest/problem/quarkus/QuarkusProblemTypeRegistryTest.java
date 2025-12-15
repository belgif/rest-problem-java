package io.github.belgif.rest.problem.quarkus;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.InternalServerErrorProblem;

class QuarkusProblemTypeRegistryTest {

    @Test
    void getProblemTypesEmpty() {
        QuarkusProblemTypeRegistry registry = new QuarkusProblemTypeRegistry(new Class<?>[0]);
        assertThat(registry.getProblemTypes()).isEmpty();
    }

    @Test
    void getProblemTypes() {
        QuarkusProblemTypeRegistry registry =
                new QuarkusProblemTypeRegistry(new Class<?>[] {
                        BadRequestProblem.class, InternalServerErrorProblem.class });
        assertThat(registry.getProblemTypes())
                .containsEntry("urn:problem-type:belgif:badRequest", BadRequestProblem.class)
                .containsEntry("urn:problem-type:belgif:internalServerError", InternalServerErrorProblem.class);
    }

}
