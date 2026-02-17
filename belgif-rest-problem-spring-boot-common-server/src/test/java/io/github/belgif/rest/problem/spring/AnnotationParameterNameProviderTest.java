package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class AnnotationParameterNameProviderTest {

    private final AnnotationParameterNameProvider provider = new AnnotationParameterNameProvider();

    @Test
    void toResult() {
        assertThat(provider.toResult(Stream.of("A", "B"))).containsExactly("A", "B");
    }

}
