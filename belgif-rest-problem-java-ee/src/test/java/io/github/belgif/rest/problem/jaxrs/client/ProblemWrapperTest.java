package io.github.belgif.rest.problem.jaxrs.client;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

import io.github.belgif.rest.problem.BadRequestProblem;
import io.github.belgif.rest.problem.api.Problem;

class ProblemWrapperTest {

    @Test
    void problemWrapper() {
        Problem problem = new BadRequestProblem();

        ProblemWrapper wrapper = new ProblemWrapper(problem);

        assertThat(wrapper.getProblem()).isSameAs(problem);
        assertThat(wrapper.getCause()).isSameAs(problem);
        assertThat(wrapper.getResponse()).isNull();
    }

}
