package io.github.belgif.rest.problem.spring;

import static org.assertj.core.api.Assertions.*;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class AnnotationParameterNameDiscovererTest {

    private final AnnotationParameterNameDiscoverer discoverer = new AnnotationParameterNameDiscoverer();

    @Test
    void toResult() {
        assertThat(discoverer.toResult(Stream.of("A", "B"))).containsExactly("A", "B");
    }

}
