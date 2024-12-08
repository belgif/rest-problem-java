package io.github.belgif.rest.problem.quarkus;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.jsontype.NamedType;

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
        assertThat(registry.getProblemTypes()).containsExactly(
                new NamedType(BadRequestProblem.class, "urn:problem-type:belgif:badRequest"),
                new NamedType(InternalServerErrorProblem.class, "urn:problem-type:belgif:internalServerError"));
    }

}
