package io.github.belgif.rest.problem.ee.internal;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Test;

class PlatformTest {

    @Test
    void isQuarkus() {
        assertThat(Platform.isQuarkus()).isFalse();
    }

    @Test
    void classPathContains() {
        assertThat(Platform.classPathContains("java.lang.String")).isTrue();
        assertThat(Platform.classPathContains("foo.Bar")).isFalse();
    }

}
